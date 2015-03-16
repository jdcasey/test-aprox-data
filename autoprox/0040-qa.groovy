
import org.commonjava.aprox.autoprox.data.*;

import java.net.MalformedURLException;

import org.commonjava.aprox.model.core.*;

class QARule extends AbstractAutoProxRule
{
    // QA-EAP-6.3.0.ER1 ==> EAP/6.3.0/ER1
    // QA-EAP-6.3.0-ER1 ==> EAP/6.3.0/ER1
    // QAx-EAP-6.3.0.ER1 => EAP/6.3.0/ER1
    // QAx-EAP-6.3.0.ER1 => EAP/6.3.0/ER1

    //           QA x  -  EAP     -   6.3.0         .    ER1
    //              1      2            3                 4
    def REGEX = /QA(x)?-([^0-9]+)-([-.0-9a-zA-Z]+)[.-](.R\d+)/

    boolean matches( String named )
    {
        return named =~ REGEX;
    }

    RemoteRepository createRemoteRepository( String named )
        throws MalformedURLException
    {
        def match = (named =~ REGEX)[0]
        def rr = new RemoteRepository( name: named, url: "http://download.lab.bos.redhat.com/devel/jdcasey/staging-repo-tests/${match[2]}/${match[3]}/${match[4]}/" )

        rr.setDescription( "QA proxy to staged repository for ${match[2]}-${match[3]}.${match[4]}" )

        rr
    }

    Group createGroup( String named )
    {
        Group g = new Group( named );
        g.addConstituent( new StoreKey( StoreType.remote, "RH-all" ) )

        def match = (named =~ REGEX)[0]
        def description = "QA proxy to staged repository for ${match[2]}-${match[3]}.${match[4]}"

        // if 'x', then add the public repo group as well.
        if ( match[1] ){
          g.addConstituent( new StoreKey( StoreType.group, 'public' ) )
          description += " INCLUDING PUBLIC GROUP"
        }

        g.addConstituent( new StoreKey( StoreType.remote, named ) )

        g.setDescription( description )

        g
    }
}
