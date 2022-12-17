package com.livingtechusa.reflexion

import com.livingtechusa.reflexion.util.ReflexionArrayItem
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {


    var count = 0L

    @Test
    fun addLevel() {
        val item1 = ReflexionArrayItem(
            itemPK = count,
            itemName = "first",
            children = mutableListOf()
        )
        count += 1L
        val newFirst = ReflexionArrayItem(
            itemPK = count,
            itemName = count.toString(),
            children = mutableListOf()
        )
        count += 1L
        val newSecond = ReflexionArrayItem(
            itemPK = count,
            itemName = count.toString(),
            children = mutableListOf()
        )
        val list = mutableListOf<ReflexionArrayItem?>()
        list.add(newFirst)
        list.add(newSecond)
        val result = newLevel(item1, list)
        println(result)
    }

    fun newLevel(
        Rai: ReflexionArrayItem,
        list: MutableList<ReflexionArrayItem?>
    ): ReflexionArrayItem {
        Rai.items = list
        return Rai
    }

    val item1 = ReflexionArrayItem(
        itemPK = count,
        itemName = "first",
        children = mutableListOf()
    )
    @Test
    fun addMultipleLevels() {

        val list = getTwoMore()
        list.forEach() {
            it?.items = getTwoMore()
        }
        val result = newLevel(item1, list)
        println(result)
    }

    fun getTwoMore(): MutableList<ReflexionArrayItem?> {
        count += 1L
        val newFirst = ReflexionArrayItem(
            itemPK = count,
            itemName = count.toString(),
            children = mutableListOf()
        )
        count += 1L
        val newSecond = ReflexionArrayItem(
            itemPK = count,
            itemName = count.toString(),
            children = mutableListOf()
        )
        val list = mutableListOf<ReflexionArrayItem?>()
        list.add(newFirst)
        list.add(newSecond)
        return list
    }
}