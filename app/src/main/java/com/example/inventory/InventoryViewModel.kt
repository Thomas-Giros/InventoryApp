package com.example.inventory

import androidx.lifecycle.*
import com.example.inventory.data.Item
import com.example.inventory.data.ItemDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class InventoryViewModel(private val itemDao: ItemDao) : ViewModel() {

    val allItems: LiveData<List<Item>> = itemDao.getAll().asLiveData()


    private fun insertItem(item: Item) {
        viewModelScope.launch {
            itemDao.insert(item)
        }
    }
    private fun getNewItemEntry(itemName: String, itemPrice: String, itemCount: String): Item {
        return Item(
            itemName = itemName,
            itemPrice = itemPrice.toDouble(),
            quantityInStock = itemCount.toInt()
        )
    }

    fun addNewItem(itemName: String, itemPrice: String, itemCount: String) {
        val newItem = getNewItemEntry(itemName, itemPrice, itemCount)
        insertItem(newItem)
    }

     private fun updateItem(item: Item) {
        viewModelScope.launch {
            itemDao.update(item)
        }
    }

    fun updateItem(itemId: Int,
                   itemName: String,
                   itemPrice: String,
                   itemCount: String) {
        val updatedItem = Item(id = itemId,
                               itemName = itemName,
                               itemPrice = itemPrice.toDouble(),
                               quantityInStock = itemCount.toInt())
        updateItem(updatedItem)
    }


    fun sellItem(item: Item) {
        if (item.quantityInStock > 0) {
            // Decrease the quantity by 1
            val newItem = item.copy(quantityInStock = item.quantityInStock - 1)
            updateItem(newItem)
        }
    }

    fun isStockAvailable(item: Item): Boolean {
        return (item.quantityInStock > 0)
    }


    fun deleteItem(item: Item) {
        viewModelScope.launch {
            itemDao.delete(item)
        }
    }

    fun retrieveItem(id: Int): LiveData<Item> {
        return itemDao.get(id).asLiveData()
    }

    fun isEntryValid(itemName: String, itemPrice: String, itemCount: String): Boolean {
        if (itemName.isBlank() || itemPrice.isBlank() || itemCount.isBlank()) {
            return false
        }
        return true
    }

}

class InventoryViewModelFactory(private val itemDao: ItemDao) : ViewModelProvider.Factory {
    /**
     * Creates a new instance of the given `Class`.
     *
     * @param modelClass a `Class` whose instance is requested
     * @return a newly created ViewModel
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InventoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InventoryViewModel(itemDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")

    }
}
