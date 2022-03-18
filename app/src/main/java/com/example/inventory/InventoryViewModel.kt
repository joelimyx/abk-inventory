package com.example.inventory

import androidx.lifecycle.*
import com.example.inventory.data.Item
import com.example.inventory.data.ItemDao
import kotlinx.coroutines.launch

class InventoryViewModel(private val itemDao: ItemDao): ViewModel() {

    val allItems: LiveData<List<Item>> = itemDao.getAllItem().asLiveData()

    private fun insertItem(item: Item){
        viewModelScope.launch {
            itemDao.insert(item)
        }
    }
    private fun getNewItemEntry(itemName: String, itemPrice: String, itemCount: String): Item{
        return Item(
            itemName = itemName,
            itemPrice = itemPrice.toDouble(),
            itemQuantity = itemCount.toInt()
        )
    }

    fun addNewItem(itemName: String, itemPrice: String, itemCount: String){
        val newItem = getNewItemEntry(itemName,itemPrice, itemCount)
        insertItem(newItem)
    }

    fun retrieveItem(id: Int): LiveData<Item>{
        return itemDao.getItem(id).asLiveData()
    }

    private fun updateItem(item: Item){
        viewModelScope.launch { itemDao.update(item) }
    }

    fun sellItem(item: Item){
        if (item.itemQuantity >0){
            val newItem = item.copy(itemQuantity = item.itemQuantity-1)
            updateItem(newItem)
        }
    }

    fun deleteItem(item: Item){
        viewModelScope.launch {
            itemDao.delete(item)
        }
    }

    fun isStockAvailable(item: Item): Boolean{
        return item.itemQuantity > 0
    }

    private fun getUpdatedItemEntry(itemId: Int, itemName: String, itemPrice: String, itemCount: String): Item{
        return Item(itemId, itemName, itemPrice.toDouble(), itemCount.toInt())
    }
    fun updateItem(itemId: Int, itemName: String, itemPrice: String, itemCount: String){
        val updatedItem = getUpdatedItemEntry(itemId, itemName, itemPrice, itemCount)
        updateItem(updatedItem)
    }
    fun isEntryValid(itemName: String, itemPrice: String, itemCount: String): Boolean{
        return !(itemName.isBlank() || itemPrice.isBlank() || itemCount.isBlank())
    }

    class InventoryViewModelFactory(private val itemDao: ItemDao): ViewModelProvider.Factory{
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(InventoryViewModel::class.java)){
                @Suppress("UNCHECKED_CAST")
                return InventoryViewModel(itemDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}