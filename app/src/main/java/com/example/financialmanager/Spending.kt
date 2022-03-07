package com.example.financialmanager
import java.sql.Timestamp

class Spending {
    var product :String?= null
    var category: String?= null
    var price: Int?= null
    var time: String?= null
    constructor(){}

    constructor(
        product:String?,
        category:String?,
        price:Int?,
        time:String?,
    ){
        this.product = product
        this.category = category
        this.price = price
        this.time = time
    }

}

