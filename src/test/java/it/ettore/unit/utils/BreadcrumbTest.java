package it.ettore.unit.utils;
import it.ettore.utils.Breadcrumb;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BreadcrumbTest {
    Breadcrumb breadcrumb = new Breadcrumb("Home", "/");
    @Test
    public void getName() {
        assertEquals("Home", breadcrumb.getName());
    }

    @Test
    public void getUrl() {
        assertEquals("/", breadcrumb.getUrl());
    }

    @Test
    public void testToString() {
        assertEquals("Breadcrumb(name=Home, url=/)", breadcrumb.toString());
    }

}
