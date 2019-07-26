package vn.android.thn.commons

class QueryNativeEntity {
    var firstResult: Int = 0
    var maxResults: Int = 20
    var params: MutableList<Any> = ArrayList<Any>()
    var listParameter = HashMap<String, List<*>>()
    var queryNativeString = ""
    var isListResult = true
    fun initParam(){
        firstResult = 0
        maxResults  = 20
        params = ArrayList<Any>()
        listParameter = HashMap<String, List<*>>()
        queryNativeString = ""
        isListResult = true
    }
}