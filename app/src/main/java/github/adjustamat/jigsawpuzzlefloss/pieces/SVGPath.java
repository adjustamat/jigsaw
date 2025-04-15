package github.adjustamat.jigsawpuzzlefloss.pieces;

import android.graphics.Path;

import java.util.ArrayList;

public class SVGPath {

    private ArrayList<Segment> list;
    //private int i;
    //private Rotate rotate;

    public enum Rotate {
        CLOCKWISE,
        COUNTERCLOCKWISE,
        TWICE,
        FLIP
        // , CW_FLIP, CCW_FLIP, TWICE_FLIP;
    }

    public static class Segment {

        public final float[] data;

        public Segment(float[] allData, int dataOffset, boolean from102to60) {

            this.data = new float[6];
            for (int i = 0; i < data.length; i++) {
                data[i] = allData[dataOffset + i];
                if (from102to60)
                    data[i] /= 1.7f;
            }
        }

        public void toPath(Path path) {
//            switch(verb){
//            case LINE:
//                path.rLineTo(data[0],data[1]);
//                break;
//            case CUBIC:
            path.rCubicTo(data[0], data[1], data[2], data[3], data[4], data[5]);
//                break;
//            }
        }

        void flip() {
//            switch(verb){
//                case CUBIC:
            data[3] = -data[3];
            data[5] = -data[5];
//                case LINE:
            data[1] = -data[1];
//            }
        }

        public Segment clone() {
            return new Segment(verb, data, 0, false);
        }
    } // class Segment

    static SVGPath[][] generateAll() {
        SVGPath[][] ret = new SVGPath[3 * 3 * 2][];
        int reti = 0;
        for (float neckWidth = 0f; neckWidth < 13f; neckWidth += 5f) { // from narrow to wide
            for (float curvature = 0f; curvature < 6.25f; curvature += 2.5f) { // from straight to curved
                ret[reti] = generate_cfirst(neckWidth, curvature);
                reti++;
                ret[reti] = generate_csecond(neckWidth, curvature);
                reti++;
            }
        }
        // TODO: add these SVGPaths to the pool from which to get ready-rotated cfirst paths with the selected neckWidth and curvature
        return ret;
    }

    static SVGPath[] generate_csecond(float neckWidth, float curvature) {
        SVGPath csecond_north_out = new SVGPath(new float[]{
                15f, 0f,/*<-cp1*/ 22.5f, 7.5f,/*<-cp2*/ 22.5f, 17.5f,
                0f, -10f,/*<-cp1*/ -17.5f + neckWidth, -17.5f,/*<-cp2*/ -15 + neckWidth, 25,
                2.5f, 7.5f,/*<-cp1*/ 32.5f - neckWidth, 7.5f + curvature,/*<-cp2*/ 47.5f - neckWidth, 7.5f + curvature,
                5f, 0f,/*<-cp1*/ 45f, -curvature,/*<-cp2*/ 45f, -curvature
        });
        SVGPath csecond_north_in = new SVGPath(csecond_north_out).doRotate(Rotate.FLIP);
        SVGPath csecond_east_out = new SVGPath(csecond_north_out).doRotate(Rotate.CLOCKWISE);
        SVGPath csecond_west_out = new SVGPath(csecond_north_out).doRotate(Rotate.COUNTERCLOCKWISE);
        SVGPath csecond_south_out = new SVGPath(csecond_north_out).doRotate(Rotate.TWICE);
        SVGPath csecond_east_in = new SVGPath(csecond_north_in).doRotate(Rotate.CLOCKWISE);
        SVGPath csecond_west_in = new SVGPath(csecond_north_in).doRotate(Rotate.COUNTERCLOCKWISE);
        SVGPath csecond_south_in = new SVGPath(csecond_north_in).doRotate(Rotate.TWICE);
        return new SVGPath[]{
                csecond_north_out, csecond_west_out, csecond_south_out, csecond_east_out,
                csecond_north_in, csecond_west_in, csecond_south_in, csecond_east_in
        };
    }

    static SVGPath[] generate_cfirst(float neckWidth, float curvature) {
        SVGPath cfirst_north_out = new SVGPath(new float[]{
                0f, 0f,/*<-cp1*/ 40f, curvature,/*<-cp2*/ 45f, curvature,
                15f, 0f,/*<-cp1*/ 45f - neckWidth, -curvature,/*<-cp2*/ 47.5f - neckWidth, -7.5f - curvature,
                2.5f, -7.5f,/*<-cp1*/ -15 + neckWidth, -15f,/*<-cp2*/ -15 + neckWidth, -25f,
                0f, -10f,/*<-cp1*/ 7.5f, -17.5f,/*<-cp2*/ 22.5f, -17.5f
        });
        SVGPath cfirst_north_in = new SVGPath(cfirst_north_out);
        cfirst_north_in.doRotate(Rotate.FLIP);

        SVGPath cfirst_east_out = new SVGPath(cfirst_north_out);
        cfirst_east_out.doRotate(Rotate.CLOCKWISE);

        SVGPath cfirst_west_out = new SVGPath(cfirst_north_out);
        cfirst_west_out.doRotate(Rotate.COUNTERCLOCKWISE);

        SVGPath cfirst_south_out = new SVGPath(cfirst_north_out);
        cfirst_south_out.doRotate(Rotate.TWICE);

        SVGPath cfirst_east_in = new SVGPath(cfirst_north_in);
        cfirst_east_in.doRotate(Rotate.CLOCKWISE);

        SVGPath cfirst_west_in = new SVGPath(cfirst_north_in);
        cfirst_west_in.doRotate(Rotate.COUNTERCLOCKWISE);

        SVGPath cfirst_south_in = new SVGPath(cfirst_north_in);
        cfirst_south_in.doRotate(Rotate.TWICE);

        return new SVGPath[]{
                cfirst_north_out, cfirst_west_out, cfirst_south_out, cfirst_east_out,
                cfirst_north_in, cfirst_west_in, cfirst_south_in, cfirst_east_in
        };
    }

    public SVGPath(SVGPath copy) {
        list = new ArrayList<>();
        for (Segment segment : copy.list) {
            list.add(segment.clone());
        }
    }

    public SVGPath(float[] allData) {//, Verb[] verbs
        list = new ArrayList<>(4);
        for (int i = 0; i < allData.length; i += 6) {
            list.add(new Segment(allData, i, true));
        }
//        int i = 0;
//        for (Verb v : verbs) {
//            list.add(new Segment(v, allData, i, true));
//            i += v.dataPoints;
//        }
        //i = 0;
    }

    public SVGPath doRotate(Rotate r) {
        switch (r) {
            case FLIP:
                // TODO: use Segment.flip() (with regards to the "inside out" axis)

                break;

        /*case CW_FLIP:
            doRotate(Rotate.FLIP);*/
            case CLOCKWISE:
                // TODO: rotate +90 degrees (around point something)

                break;

            //case CCW_FLIP:
            //    doRotate(Rotate.FLIP);
            case COUNTERCLOCKWISE:
                // TODO: rotate -90 degrees (around point something)

                break;

            //case TWICE_FLIP:
            //    doRotate(Rotate.FLIP);
            case TWICE:
                // TODO: rotate 180 degrees (around point something)

                //break;
        }
        return this;
    }

    public SVGPath append(SVGPath nextPath) {
        list.addAll(nextPath.list);
        /*nextPath.resetTo(0);
        while(nextPath.hasNext()){
            list.add(nextPath.next());
        }*/
        return this;
    }

    public Path toPath(float startX, float startY) {
        Path ret = new Path();
        ret.moveTo(startX, startY);
        //i = 0;
        for (Segment segment : list) {//while(hasNext()){ next().toPath(ret);
            segment.toPath(ret);
        }
        ret.close();
        return ret;
    }
}
