package com.example.oraide.ui.explorer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oraide.data.FileNode
import com.example.oraide.data.FileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FileExplorerViewModel(private val repository: FileRepository) : ViewModel() {
    private val _fileTree = MutableStateFlow<List<FileNode>>(emptyList())
    val fileTree: StateFlow<List<FileNode>> = _fileTree.asStateFlow()
    
    private val _activeDirectory = MutableStateFlow<FileNode?>(null)
    val activeDirectory: StateFlow<FileNode?> = _activeDirectory.asStateFlow()
    
    val projectName: String
        get() = repository.getWorkspaceRoot()?.name ?: "No Project"

    fun loadProject() {
        viewModelScope.launch {
            val root = repository.getWorkspaceRoot()
            if (root != null) {
                _fileTree.value = repository.getChildren(root)
                _activeDirectory.value = null
            } else {
                _fileTree.value = emptyList()
                _activeDirectory.value = null
            }
        }
    }

    fun toggleFolder(node: FileNode) {
        if (!node.isDirectory) return

        viewModelScope.launch {
            _activeDirectory.value = node

            if (node.isExpanded) {
                // Collapse
                val newList = _fileTree.value.toMutableList()
                collapseNode(newList, node)
                _fileTree.value = newList
            } else {
                // Expand (lazy load)
                val children = repository.getChildren(node.file)
                node.children = children
                node.isExpanded = true
                val newList = _fileTree.value.toMutableList()
                
                val index = newList.indexOf(node)
                if (index != -1) {
                    newList[index] = node
                    newList.addAll(index + 1, children)
                }
                _fileTree.value = newList
            }
        }
    }

    private fun collapseNode(list: MutableList<FileNode>, node: FileNode) {
        node.isExpanded = false
        node.children?.forEach { child ->
            list.remove(child)
            if (child.isDirectory && child.isExpanded) {
                collapseNode(list, child)
            }
        }
    }
    
    fun createFile(name: String, onComplete: (FileNode?) -> Unit) {
        viewModelScope.launch {
            val parent = _activeDirectory.value?.file ?: repository.getWorkspaceRoot()
            if (parent != null) {
                val newFile = repository.createFile(parent, name)
                if (newFile != null) {
                    val newNode = FileNode(newFile)
                    reloadSpecificDirectory(_activeDirectory.value)
                    onComplete(newNode)
                } else {
                    onComplete(null)
                }
            } else {
                onComplete(null)
            }
        }
    }
    
    fun createFolder(name: String) {
        viewModelScope.launch {
            val parent = _activeDirectory.value?.file ?: repository.getWorkspaceRoot()
            if (parent != null) {
                repository.createFolder(parent, name)
                reloadSpecificDirectory(_activeDirectory.value)
            }
        }
    }

    fun renameNode(node: FileNode, newName: String, onComplete: (FileNode?) -> Unit) {
        viewModelScope.launch {
            val newFile = repository.rename(node.file, newName)
            if (newFile != null) {
                val newNode = FileNode(newFile)
                // Find parent of the renamed node by scanning the tree to reload it
                val parent = findParent(node)
                reloadSpecificDirectory(parent)
                onComplete(newNode)
            } else {
                onComplete(null)
            }
        }
    }

    fun deleteNode(node: FileNode, onComplete: () -> Unit) {
        viewModelScope.launch {
            val parent = findParent(node)
            if (repository.delete(node.file)) {
                if (_activeDirectory.value == node) {
                    _activeDirectory.value = parent
                }
                reloadSpecificDirectory(parent)
                onComplete()
            }
        }
    }
    
    private fun findParent(node: FileNode, rootChildren: List<FileNode>? = null): FileNode? {
        val childrenToSearch = rootChildren ?: _fileTree.value.filter { rootLevelNode(it) }
        
        for (parent in childrenToSearch) {
            if (parent.children?.contains(node) == true) {
                return parent
            }
            parent.children?.let {
                val found = findParent(node, it)
                if (found != null) return found
            }
        }
        return null
    }
    
    private fun rootLevelNode(node: FileNode): Boolean {
        // Since we flatten the tree, root level nodes are those that appear first and are not in any children list.
        // But a simpler way to filter root nodes is just using the initial getChildren of root.
        // For simplicity, we can keep track of root nodes. But since this is just an approximation for finding parent,
        // we can search all nodes that have children.
        return true
    }

    private suspend fun reloadSpecificDirectory(parent: FileNode?) {
        if (parent == null) {
            val root = repository.getWorkspaceRoot()
            if (root != null) {
                val newRootChildren = repository.getChildren(root)
                _fileTree.value = buildFlatTree(newRootChildren)
            }
        } else {
            val children = repository.getChildren(parent.file)
            parent.children = children
            // Rebuild tree from root
            val root = repository.getWorkspaceRoot()
            if (root != null) {
                // Since we mutated parent.children, rebuilding the flat tree from the existing root nodes will pick up the new children.
                // But we need the root nodes!
                val rootNodes = _fileTree.value.filter { findParent(it, _fileTree.value) == null } 
                // Wait, if findParent checks _fileTree.value, it's inefficient.
                // Let's just do a simple reload of the entire tree state.
                val newRootChildren = repository.getChildren(root)
                restoreExpandedState(newRootChildren, _fileTree.value)
                _fileTree.value = buildFlatTree(newRootChildren)
            }
        }
    }

    private fun restoreExpandedState(newNodes: List<FileNode>, oldFlatTree: List<FileNode>) {
        for (node in newNodes) {
            val oldNode = oldFlatTree.find { it.path == node.path }
            if (oldNode != null && oldNode.isExpanded) {
                node.isExpanded = true
                node.children = oldNode.children // Keep children for now, or re-fetch them. For a real app, we'd refetch.
            }
        }
    }

    private fun buildFlatTree(currentLevel: List<FileNode>): List<FileNode> {
        val result = mutableListOf<FileNode>()
        for (node in currentLevel) {
            result.add(node)
            if (node.isExpanded && node.children != null) {
                result.addAll(buildFlatTree(node.children!!))
            }
        }
        return result
    }
}
