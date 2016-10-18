package com.example.emanuel.myapplication;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URL;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by emanuel on 10/10/16.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    private Instrumentation instrumentation;
    private CountingIdlingResource countingResource;

    @Before
    public void setUp() throws Exception {
        instrumentation = InstrumentationRegistry.getInstrumentation();
        countingResource = new CountingIdlingResource("MyRequest");
        Request.setFactory(new Request.RequestFactory() {
            @Override
            public Request makeRequest(URL url, Request.Listener listener) {
                return new Request(url, listener) {
                    @Override
                    public void run() {
                        countingResource.increment();
                        super.run();
                        countingResource.decrement();
                    }
                };
            }
        });
        Espresso.registerIdlingResources(countingResource);
    }

    @Test
    public void launchTermsAndConditionsTest() {
        Instrumentation.ActivityMonitor activityMonitor = new Instrumentation.ActivityMonitor(TermsAndConditionsActivity.class.getName(), null, false);
        instrumentation.addMonitor(activityMonitor);

        onView(withId(R.id.termsAndConditionsButton))
                .perform(ViewActions.click());

        Activity activity = instrumentation.waitForMonitor(activityMonitor);
        Assert.assertEquals(activity.getClass(), TermsAndConditionsActivity.class);
    }

    @Test
    public void weatherTest() {
        onView(withId(R.id.navigateButton)).perform(ViewActions.click());
        onView(withId(R.id.recyclerView)).check(new ViewAssertion() {
            public void check(View view, NoMatchingViewException noView) {
                assertThat(view, ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE));
            }
        });
    }

    @After
    public void tearDown() throws Exception {
        Espresso.unregisterIdlingResources(countingResource);
    }
}
