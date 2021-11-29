package com.nilmani.nilmanibatchdemo.processor

import com.nilmani.nilmanibatchdemo.model.Person
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.batch.item.ItemProcessor

class PersonItemProcessor : ItemProcessor<Person,Person> {
    val log:Logger  = LoggerFactory.getLogger(PersonItemProcessor::class.java)
    override fun process(person: Person): Person? {
        val firstName:String = person.firstName.toUpperCase()
        val lastName:String = person.lastName.toUpperCase()
        val transformedPerson = Person(firstName,lastName)
        log.info("Converting ($person) into ($transformedPerson)")
        return  transformedPerson
    }
}