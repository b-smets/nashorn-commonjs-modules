package com.coveo.nashorn_modules;

import org.junit.Test;

import java.io.File;

import javax.script.ScriptEngineManager;

import jdk.nashorn.api.scripting.NashornScriptEngine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class FilesystemFolderTest {
  private File file = new File("src/test/resources/com/coveo/nashorn_modules/test1");
  private FilesystemFolder root = FilesystemFolder.create(file, "UTF-8");

  @Test
  public void rootFolderHasTheExpectedProperties() {
    assertTrue(root.getPath().startsWith("/"));
    assertTrue(root.getPath().endsWith("/src/test/resources/com/coveo/nashorn_modules/test1/"));
    assertNull(root.getParent());
  }

  @Test
  public void getFileReturnsTheContentOfTheFileWhenItExists() {
    assertTrue(root.getFile("foo.js").contains("foo"));
  }

  @Test
  public void getFileReturnsNullWhenFileDoesNotExists() {
    assertNull(root.getFile("invalid"));
  }

  @Test
  public void getFolderReturnsAnObjectWithTheExpectedProperties() {
    Folder sub = root.getFolder("subdir");
    assertTrue(sub.getPath().startsWith("/"));
    assertTrue(
        sub.getPath().endsWith("/src/test/resources/com/coveo/nashorn_modules/test1/subdir/"));
    assertSame(root, sub.getParent());
    Folder subsub = sub.getFolder("subsubdir");
    assertTrue(subsub.getPath().startsWith("/"));
    assertTrue(
        subsub
            .getPath()
            .endsWith("/src/test/resources/com/coveo/nashorn_modules/test1/subdir/subsubdir/"));
    assertSame(sub, subsub.getParent());
  }

  @Test
  public void getFolderReturnsNullWhenFolderDoesNotExist() {
    assertNull(root.getFolder("invalid"));
  }

  @Test
  public void getFileCanBeUsedOnSubFolderIfFileExist() {
    assertTrue(root.getFolder("subdir").getFile("bar.js").contains("bar"));
  }

  @Test
  public void filesystemFolderWorksWhenUsedForReal() throws Throwable {
    NashornScriptEngine engine =
        (NashornScriptEngine) new ScriptEngineManager().getEngineByName("nashorn");
    Require.enable(engine, root);
    assertEquals("spam", engine.eval("require('./foo').bar.spam.spam"));
  }
}
