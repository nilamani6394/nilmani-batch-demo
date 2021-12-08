package com.nilmani.nilmanibatchdemo.model

class Person {
    var lastName: String =" "
    var firstName: String =" "

    constructor() {}
    constructor(firstName: String, lastName: String) {
        this.firstName = firstName
        this.lastName = lastName
    }

    override fun toString(): String {
        return "firstName: $firstName, lastName: $lastName"
    }
}
//https://www.youtube.com/watch?v=vmyzJy-dcNU

