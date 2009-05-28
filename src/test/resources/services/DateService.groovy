import org.gentsim.framework.*

date = new ServiceDescription("date")

date.method ("getDate") {
  new Date()
}
