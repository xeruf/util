import java.sql.ResultSet

inline fun ResultSet.forEach(function: ResultSet.() -> Unit) {
    while (next())
        function(this)
}

fun ResultSet.asIterable() =
    Iterable {
        object : Iterator<ResultSet> {
            var advanced = false
            var hasNext = true
            override fun hasNext(): Boolean {
                if (!advanced) {
                    hasNext = this@asIterable.next()
                    advanced = true
                }
                return hasNext
            }
            
            override fun next(): ResultSet {
                if (!advanced)
                    this@asIterable.next()
                advanced = false
                return this@asIterable
            }
            
        }
    }
