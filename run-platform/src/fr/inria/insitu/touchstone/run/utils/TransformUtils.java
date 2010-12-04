/*   TouchStone run platform is a software to run lab experiments. It is         *
 *   published under the terms of a BSD license (see details below)              *
 *   Author: Caroline Appert (appert@lri.fr)                                     *
 *   Copyright (c) 2010 Caroline Appert and INRIA, France.                       *
 *   TouchStone run platform reuses parts of an early version which were         *
 *   programmed by Jean-Daniel Fekete under the terms of a MIT (X11) Software    *
 *   License (Copyright (C) 2006 Jean-Daniel Fekete and INRIA, France)           *
 *********************************************************************************/
/* Redistribution and use in source and binary forms, with or without            * 
 * modification, are permitted provided that the following conditions are met:   *

 *  - Redistributions of source code must retain the above copyright notice,     *
 *    this list of conditions and the following disclaimer.                      *
 *  - Redistributions in binary form must reproduce the above copyright notice,  *
 *    this list of conditions and the following disclaimer in the documentation  *
 *    and/or other materials provided with the distribution.                     *
 *  - Neither the name of the INRIA nor the names of its contributors   *
 * may be used to endorse or promote products derived from this software without *
 * specific prior written permission.                                            *

 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"   *
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE     *
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE    *
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE     *
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR           *
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF          *
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS      *
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN       *
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)       *
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE    *
 * POSSIBILITY OF SUCH DAMAGE.                                                   *
 *********************************************************************************/
package fr.inria.insitu.touchstone.run.utils;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;

/**
 * <b>TransformUtils</b> provides utility methods to 
 * transform rectangles.
 * 
 */
public class TransformUtils {
    private static double[] PTS1 = new double[8];
    
    /**
     * Computes the transformation of a rectangle.
     * @param transform the transform
     * @param rectSrc the source rectangle
     * @param rectDst the destination rectangle
     * @return the destination rectangle containing the bounding box
     * of the source rectangle transformed by the transformation
     */
    public static Rectangle2D transform(
            AffineTransform transform,
            Rectangle2D rectSrc, 
            Rectangle2D rectDst) {
        if (rectDst == null) {
            rectDst = (Rectangle2D) rectSrc.clone();
        }
        
        if (rectSrc.isEmpty()) {
            rectDst.setRect(rectSrc);
            return rectDst;
        }

        double scale;

        switch (transform.getType()) {
            case AffineTransform.TYPE_IDENTITY:
                if (rectSrc != rectDst)
                    rectDst.setRect(rectSrc);
                break;

            case AffineTransform.TYPE_TRANSLATION:
                rectDst.setRect(rectSrc.getX() + transform.getTranslateX(), 
                                rectSrc.getY() + transform.getTranslateY(), 
                                rectSrc.getWidth(), 
                                rectSrc.getHeight());
                break;

            case AffineTransform.TYPE_UNIFORM_SCALE:
                scale = transform.getScaleX();
                rectDst.setRect(rectSrc.getX() * scale, 
                                rectSrc.getY() * scale, 
                                rectSrc.getWidth() * scale, 
                                rectSrc.getHeight() * scale);
                break;
                
            case AffineTransform.TYPE_TRANSLATION | AffineTransform.TYPE_UNIFORM_SCALE:
                scale = transform.getScaleX();
                rectDst.setRect((rectSrc.getX() * scale) + transform.getTranslateX(), 
                                (rectSrc.getY() * scale) + transform.getTranslateY(), 
                                rectSrc.getWidth() * scale, 
                                rectSrc.getHeight() * scale);
                break;

            default :
                double[] pts = rectToArray(rectSrc);
                transform.transform(pts, 0, pts, 0, 4);
                rectFromArray(rectDst, pts);                
                break;
        }
        
        
        return rectDst;
    }
    
    /**
     * Computes the inverse transformation of a rectangle.
     * @param transform the transform
     * @param rectSrc the source rectangle
     * @param rectDst the destination rectangle
     * @return the destination rectangle containing the bounding box
     * of the source rectangle inversly transformed by the transformation
     */
    public static Rectangle2D inverseTransform(
            AffineTransform transform,
            Rectangle2D rectSrc, 
            Rectangle2D rectDst) {
        if (rectDst == null) {
            rectDst = (Rectangle2D) rectSrc.clone();
        }
        
        if (rectSrc.isEmpty()) {
            rectDst.setRect(rectSrc);
            return rectDst;         
        }
        
        double scale;

        switch (transform.getType()) {
            case AffineTransform.TYPE_IDENTITY:
                if (rectSrc != rectDst)
                    rectDst.setRect(rectSrc);
                break;

            case AffineTransform.TYPE_TRANSLATION:
                rectDst.setRect(rectSrc.getX() - transform.getTranslateX(), 
                                rectSrc.getY() - transform.getTranslateY(), 
                                rectSrc.getWidth(), 
                                rectSrc.getHeight());
                break;

            case AffineTransform.TYPE_UNIFORM_SCALE:
                scale = 1 / transform.getScaleX();
                rectDst.setRect(rectSrc.getX() * scale, 
                                rectSrc.getY() * scale, 
                                rectSrc.getWidth() * scale, 
                                rectSrc.getHeight() * scale);
                break;
                
            case AffineTransform.TYPE_TRANSLATION | AffineTransform.TYPE_UNIFORM_SCALE:
                scale = 1 / transform.getScaleX();
                rectDst.setRect((rectSrc.getX() - transform.getTranslateX()) * scale, 
                                (rectSrc.getY() - transform.getTranslateY()) * scale, 
                                rectSrc.getWidth() * scale, 
                                rectSrc.getHeight() * scale);
                break;
                
            default :
                double[] pts = rectToArray(rectSrc);
                try {
                    transform.inverseTransform(pts, 0, pts, 0, 4);
                } catch (NoninvertibleTransformException e) {
                    e.printStackTrace();
                }       
                rectFromArray(rectDst, pts);
                break;
        }
            
        return rectDst;
    }

    private static double[] rectToArray(Rectangle2D aRectangle) {
        PTS1[0] = aRectangle.getX();
        PTS1[1] = aRectangle.getY();
        PTS1[2] = PTS1[0] + aRectangle.getWidth();
        PTS1[3] = PTS1[1];
        PTS1[4] = PTS1[0] + aRectangle.getWidth();
        PTS1[5] = PTS1[1] + aRectangle.getHeight();
        PTS1[6] = PTS1[0];
        PTS1[7] = PTS1[1] + aRectangle.getHeight();
        return PTS1;
    }

    private static void rectFromArray(Rectangle2D aRectangle, double[] pts) {
        double minX = pts[0];
        double minY = pts[1];
        double maxX = pts[0];
        double maxY = pts[1];

        double x;
        double y;

        for (int i = 1; i < 4; i++) {
            x = pts[2 * i];
            y = pts[(2 * i) + 1];
            
            if (x < minX) {
                minX = x;
            }
            if (y < minY) {
                minY = y;
            }
            if (x > maxX) {
                maxX = x;
            }
            if (y > maxY) {
                maxY = y;
            }
        }
        aRectangle.setRect(minX, minY, maxX - minX, maxY - minY);
    }   
}
