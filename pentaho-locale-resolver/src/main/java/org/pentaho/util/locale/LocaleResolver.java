/*!
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2018 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.util.locale;

import java.util.Locale;

public class LocaleResolver {

    private static final ThreadLocal<Locale> threadLocales = new ThreadLocal<>();
    private static final ThreadLocal<Locale> threadLocaleOverride = new ThreadLocal<>();

    private static Locale defaultLocale = Locale.getDefault();
    private static Locale failOverLocale = Locale.US;

    public static void setDefaultLocale( final Locale newLocale ) {
        defaultLocale = newLocale;
    }

    public static Locale getDefaultLocale() {
        return defaultLocale;
    }

    public static Locale getFailOverLocale() {
        return failOverLocale;
    }

    public static void setFailOverLocale(Locale failOverLocale) {
        LocaleResolver.failOverLocale = failOverLocale;
    }

    public static void setLocaleOverride(final Locale localeOverride ) {
        threadLocaleOverride.set( localeOverride );
    }

    public static Locale getLocaleOverride() {
        return threadLocaleOverride.get();
    }

    public static void setLocale( final Locale newLocale ) {
        threadLocales.set( newLocale );
    }

    public static Locale getLocale() {
        Locale override = threadLocaleOverride.get();
        if ( override != null ) {
            return override;
        }
        Locale rtn = threadLocales.get();
        if ( rtn != null ) {
            return rtn;
        }
        defaultLocale = Locale.getDefault();
        setLocale( defaultLocale );
        return defaultLocale;
    }

    public static String getClosestLocale( String locale, String[] locales ) {
        // see if this locale is supported
        if ( locales == null || locales.length == 0 ) {
            return locale;
        }
        if ( locale == null || locale.length() == 0 ) {
            return locales[0];
        }
        String localeLanguage = locale.substring( 0, 2 );
        String localeCountry = ( locale.length() > 4 ) ? locale.substring( 0, 5 ) : localeLanguage;
        int looseMatch = -1;
        int closeMatch = -1;
        int exactMatch = -1;
        for ( int idx = 0; idx < locales.length; idx++ ) {
            if ( locales[idx].equals( locale ) ) {
                exactMatch = idx;
                break;
            } else if ( locales[idx].length() > 1 && locales[idx].substring( 0, 2 ).equals( localeLanguage ) ) {
                looseMatch = idx;
            } else if ( locales[idx].length() > 4 && locales[idx].substring( 0, 5 ).equals( localeCountry ) ) {
                closeMatch = idx;
            }
        }
        //CHECKSTYLE IGNORE EmptyBlock FOR NEXT 3 LINES
        if ( exactMatch != -1 ) {
            // do nothing we have an exact match
        } else if ( closeMatch != -1 ) {
            locale = locales[closeMatch];
        } else if ( looseMatch != -1 ) {
            locale = locales[looseMatch];
        } else {
            // no locale is close , just go with the first?
            locale = locales[0];
        }
        return locale;
    }
}
