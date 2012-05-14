package org.eclipse.jetty.osgi.boot.internal.webapp;


import org.eclipse.jetty.osgi.boot.utils.BundleFileLocatorHelper;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

/**
 * BundleFileLocatorHelperFactory
 *
 * Obtain a helper for locating files based on the bundle.
 */
public class BundleFileLocatorHelperFactory
{ 
    private static final Logger LOG = Log.getLogger(BundleFileLocatorHelperFactory.class);
    
    private static BundleFileLocatorHelperFactory _instance = new BundleFileLocatorHelperFactory();
    
    private BundleFileLocatorHelperFactory() {}
    
    public static BundleFileLocatorHelperFactory getFactory()
    {
        return _instance;
    }
    
    public BundleFileLocatorHelper getHelper()
    {
        BundleFileLocatorHelper helper = BundleFileLocatorHelper.DEFAULT;
        try
        {
            //see if a fragment has supplied an alternative
            helper = (BundleFileLocatorHelper) Class.forName(BundleFileLocatorHelper.CLASS_NAME).newInstance();
        }
        catch (Throwable t)
        {
            LOG.ignore(t);
        }
        return helper;
    }

}