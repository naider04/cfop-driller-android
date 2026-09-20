package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("CFOP Drill", appName)
  }

  @Test
  fun `verify algorithm parsing for standard algorithm`() {
    val steps = com.example.parser.AlgorithmParser.parseAlgorithmMoves("x R' U R' D2 R U' R' D2 R2 x'")
    assertEquals(11, steps.size)
    assertEquals("x", steps[0].spoken)
    assertEquals("R prime", steps[1].spoken)
    assertEquals("D two", steps[4].spoken)
    assertEquals("x prime", steps[10].spoken)
  }

  @Test
  fun `verify algorithm parsing for wide turns`() {
    val wideSteps = com.example.parser.AlgorithmParser.parseAlgorithmMoves("r U R' U' r' F R F'")
    assertEquals(8, wideSteps.size)
    assertEquals("wide R", wideSteps[0].spoken)
    assertEquals("wide R prime", wideSteps[4].spoken)
  }
}
