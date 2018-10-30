package org.highjack.scalapipeline.pipeline

    class JsonPipelineVM extends java.io.Serializable {
        private var base64 : String = null
        def this(base64: String) {
            this()
            this.base64 = base64
        }
        def getBase64(): String = base64
        def setBase64(base64: String): Unit = {
            this.base64 = base64
        }
    }
