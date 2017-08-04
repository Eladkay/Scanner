package eladkay.scanner.misc

import com.teamwizardry.librarianlib.common.util.saving.serializers.Serializer
import com.teamwizardry.librarianlib.common.util.saving.serializers.SerializerRegistry
import com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.Targets
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos

class ScannerSerializers {
    init {
        SerializerRegistry.register("minecraft:mutblockpos", Serializer(BlockPos.MutableBlockPos::class.java))
        SerializerRegistry["minecraft:mutblockpos"]!!.register(Targets.NBT,
                Targets.NBT.impl<BlockPos.MutableBlockPos>({ nbtBase, o, aBoolean -> BlockPos.MutableBlockPos(BlockPos.fromLong((nbtBase as NBTTagCompound).getLong("long"))) }
                ) { `val`, syncing ->
                    val tag = NBTTagCompound()
                    tag.setLong("long", `val`.toLong())
                    tag
                })
        SerializerRegistry["minecraft:mutblockpos"]?.register(Targets.BYTES, Targets.BYTES.impl<BlockPos.MutableBlockPos>
        ({ buf, existing, sync ->
            BlockPos.MutableBlockPos(buf.readInt(), buf.readInt(), buf.readInt())
        }, { buf, value, sync ->
            buf.writeInt(value.x)
            buf.writeInt(value.y)
            buf.writeInt(value.z)
        }))

//        SerializerRegistry.register("java:generator.q", Serializer(ArrayDeque::class.java))
//
//        SerializerRegistry["java:generator.q"]?.register(Targets.NBT, { type ->
//            type as FieldTypeGeneric
//            val typeParam = type.generic(0)!!
//            val subSerializer = SerializerRegistry.lazyImpl(Targets.NBT, typeParam)
//
//            @Suppress("UNCHECKED_CAST")
//            val constructorMH = MethodHandleHelper.wrapperForConstructor(type.clazz.getConstructor()) as (Array<Any>) -> ArrayDeque<Any?>
//
//            Targets.NBT.impl<ArrayDeque<*>>({ nbt, existing, syncing ->
//                val list = nbt.safeCast(NBTTagList::class.java)
//
//                @Suppress("UNCHECKED_CAST")
//                val array = (existing ?: constructorMH(arrayOf())) as ArrayDeque<Any?>
//
//                while (array.size > list.tagCount())
//                    array.pop()
//
//                list.forEachIndexed<NBTTagCompound> { i, container ->
//                    val tag = container.getTag("-")
//                    val v = if (tag == null) null else subSerializer().read(tag, array.filterIndexed { index, any -> index == i}.getOrNull(0), syncing)
//                    if (i >= array.size) {
//                        array.add(v)
//                    } else {
//                        val bak = array.map { it }
//                        array.clear()
//                        bak.mapIndexedTo(array) { index, any -> if(index == i) v else any }
//                    }
//                }
//
//                array
//            }, { value, syncing ->
//                val list = NBTTagList()
//
//                for (i in 0..value.size - 1) {
//                    val container = NBTTagCompound()
//                    list.appendTag(container)
//                    val v = value.filterIndexed { index, any -> index == i}.getOrNull(0)
//                    if (v != null) {
//                        container.setTag("-", subSerializer().write(v, syncing))
//                    }
//                }
//
//                list
//            })
//        })
    }
}
