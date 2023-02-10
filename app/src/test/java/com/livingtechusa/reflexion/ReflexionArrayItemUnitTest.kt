package com.livingtechusa.reflexion

import com.livingtechusa.reflexion.data.models.ReflexionArrayItem
import org.junit.Test

/**
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ReflexionArrayItemUnitTest {

    private var count = 0L

    @Test
    fun addLevel() {
        val item1 = ReflexionArrayItem(
            itemPK = count,
            itemName = "first",
            children = mutableListOf(),
            nodePk = null
        )
        count += 1L
        val newFirst = ReflexionArrayItem(
            itemPK = count,
            itemName = count.toString(),
            children = mutableListOf(),
            nodePk = null
        )
        count += 1L
        val newSecond = ReflexionArrayItem(
            itemPK = count,
            itemName = count.toString(),
            children = mutableListOf(),
            nodePk = null
        )
        val list = mutableListOf<ReflexionArrayItem>()
        list.add(newFirst)
        list.add(newSecond)
        val result = newLevel(item1, list)
        println(result)
    }

    private fun newLevel(
        Rai: ReflexionArrayItem,
        list: MutableList<ReflexionArrayItem>,
    ): ReflexionArrayItem {
        Rai.children = list
        return Rai
    }

    val item1 = ReflexionArrayItem(
        itemPK = count,
        itemName = "first",
        children = mutableListOf(),
        nodePk = null
    )
    @Test
    fun addMultipleLevels() {

        val list = getTwoMore()
        list.forEach() {
            it?.children = getTwoMore()
        }
        val result = newLevel(item1, list)
        println(result)
    }

    fun getTwoMore(): MutableList<ReflexionArrayItem> {
        count += 1L
        val newFirst = ReflexionArrayItem(
            itemPK = count,
            itemName = count.toString(),
            children = mutableListOf(),
            nodePk = null
        )
        count += 1L
        val newSecond = ReflexionArrayItem(
            itemPK = count,
            itemName = count.toString(),
            children = mutableListOf(),
            nodePk = null
        )
        val list = mutableListOf<ReflexionArrayItem>()
        list.add(newFirst)
        list.add(newSecond)
        return list
    }
}