package com.example.salesapp.Customer

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.salesapp.*
import com.example.salesapp.API.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CustomerViewModel: ViewModel() {

    private val _customers = MutableLiveData<ArrayList<GetCustomerResponse>?>()
    val customers: MutableLiveData<ArrayList<GetCustomerResponse>?> get() = _customers
    var customerNamesList: MutableLiveData<List<String>> = MutableLiveData()

     init {
         customerNamesList.value = mutableListOf<String>("Select a Customer")
     }

    fun fetchCustomers(salesUsername: String) {
        RetrofitClient.customers_instance.getCustomers(salesUsername).enqueue(object : Callback<ArrayList<GetCustomerResponse>> {
            override fun onResponse(
                call: Call<ArrayList<GetCustomerResponse>>,
                response: Response<ArrayList<GetCustomerResponse>>
            ) {
                if (response.isSuccessful) {
                    Log.d("CustomerFragment","Fetch Customer Berhasil!")
                    val customerList = response.body()
                    _customers.value = customerList
                    val customerNameList = mutableListOf<String>("Select a Customer")
                    if (customerList != null) {
                        for (customer in customerList) {
                            customerNameList.add(customer.name)
                        }
                        customerNamesList.value = customerNameList
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<GetCustomerResponse>>, t: Throwable) {
            }
        })
    }

    fun addCustomer(customer: PostCustomerRequest, salesUsername: String) {
        RetrofitClient.customers_instance.createCustomer(customer)
            .enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (response.isSuccessful) {
                        Log.d("CustomerFragment","Add Customer Berhasil!")
                        fetchCustomers(salesUsername)
                    }
                }
                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                }
            })
    }

    fun unsubscribeCustomer(customer: PatchCustomerRequest, salesUsername: String) {
        RetrofitClient.customers_instance.unsubscribeCustomer(customer)
            .enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (response.isSuccessful) {
                        fetchCustomers(salesUsername)
                    } else {
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                }
            })
    }

    fun sortCustomersByName() {
        val sortedList = customers.value?.toMutableList()?.apply {
            sortBy { it.name }
        }
        customers.value = sortedList as ArrayList<GetCustomerResponse>?
    }

    fun sortCustomersByAddress() {
        val sortedList = customers.value?.toMutableList()?.apply {
            sortBy { it.address }
        }
        customers.value = sortedList as ArrayList<GetCustomerResponse>?
    }

    fun sortCustomerList(sortOption: String) {
        when (sortOption) {
            "Sort By Name" -> sortCustomersByName()
            "Sort By Address" -> sortCustomersByAddress()
        }
    }




}
