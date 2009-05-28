import org.gentsim.framework.*

cat = new EntityDescription("cat")
//cat.attribute "size", AnimalSize.small
cat.method ("makeNoise") { println "meow" }
cat.usesService ("litter-box")

dog = new EntityDescription("dog")
//dog.attribute "size", AnimalSize.medium
dog.method ("makeNoise") { println "woof" }

cow = new EntityDescription("cow")
//cow.attribute "size", AnimalSize.medium
cow.method ("makeNoise") { println "moo" }

