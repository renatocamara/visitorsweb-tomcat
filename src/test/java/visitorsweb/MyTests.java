package visitorsweb;

import static org.junit.matchers.JUnitMatchers.containsString;
import static org.junit.matchers.JUnitMatchers.everyItem;
import static org.junit.matchers.JUnitMatchers.hasItems;

import java.util.Arrays;

import org.junit.Test;

public class MyTests {

	@Test
	public void testAssertEquals() {
		org.junit.Assert.assertEquals("failure - strings are not equal", "text", "text");
	}

	@Test
	public void testAssertFalse() {
		org.junit.Assert.assertFalse("failure - should be false", false);
	}
	
	@Test
	public void testAssertTrue() {
		org.junit.Assert.assertTrue("failure - should be true", true);
	}

	@Test
	public void testAssertNotNull() {
		org.junit.Assert.assertNotNull("should not be null", new Object());
	}

	@Test
	public void testAssertNull() {
		org.junit.Assert.assertNull("should be null", null);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testAssertThathasItemsContainsString() {
		org.junit.Assert.assertThat(Arrays.asList("one", "two", "three"), hasItems("one", "two", "three"));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testAssertThatEveryItemContainsString() {
		org.junit.Assert.assertThat(
				Arrays.asList(new String[] { "fun", "ban", "net" }), everyItem(containsString("n")));
	}

	
}