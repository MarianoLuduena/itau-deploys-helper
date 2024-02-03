package com.itau.deploys.http

trait HttpWrapper {

  protected def doWithHttpClient(fn: WebClient => Unit): Unit = {
    val startExecutionTime = System.currentTimeMillis()
    val backend = HttpBackend.backend()
    val webClient = WebClient(backend)

    try {
      fn(webClient)
    } catch {
      case e: Throwable => e.printStackTrace()
    }

    val endExecutionTime = System.currentTimeMillis()
    println(s"Execution Time: ${endExecutionTime - startExecutionTime} milliseconds")
    backend.close()
  }

}
