package npci.org.data

import org.scalatest.funsuite.AnyFunSuite


class SampleIntegrationTest  extends  AnyFunSuite{

  test("Integration Test An empty Set should have size 0") {
    assert(Set.empty.size == 0)
  }

}
