package com.avocado.expenses.shared

class DesktopPlatform : Platform {
  override val name: String = System.getProperty("os.name")
}

actual fun getPlatform(): Platform = DesktopPlatform()