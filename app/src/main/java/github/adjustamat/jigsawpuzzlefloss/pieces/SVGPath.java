package github.adjustamat.jigsawpuzzlefloss.pieces;

import android.graphics.Path;

public class SVGPath /*implements Iterator<Segment>*/{
/*VERB_MOVE: 1 pair (indices 0 to 1)
VERB_LINE: 2 pairs (indices 0 to 3)
VERB_QUAD: NOT USED
VERB_CONIC: NOT USED
VERB_CUBIC: 4 pairs (indices 0 to 7)
VERB_CLOSE: 0 pairs
VERB_DONE: 0 pairs

void	moveTo(float x, float y)
Set the beginning of the next contour to the point (x,y).
(void	rMoveTo(float dx, float dy)
Set the beginning of the next contour relative to the last point on the previous contour.) (NOT USED)

void	rLineTo(float dx, float dy)
Add a line from the last point to the specified point (x,y).
The coordinates are considered relative to the last point on this contour. If there is no previous point, then a moveTo(0,0) is inserted automatically.

void	rCubicTo(float x1, float y1, float x2, float y2, float x3, float y3)
Add a cubic bezier from the last point, approaching control points (x1,y1) and (x2,y2), and ending at (x3,y3).
The coordinates are considered relative to the current point on this contour. If there is no previous point, then a moveTo(0,0) is inserted automatically.

void	close()
Close the current contour. If the current point is not equal to the first point of the contour, a line segment is automatically added.


void	offset(float dx, float dy, Path dst)
Offset the path by (dx,dy) - dst	Path: The translated path is written here. If this is null, then the original path is modified.
*/
    public static enum Rotate{
        CLOCKWISE, COUNTERCLOCKWISE, TWICE, 
        FLIP; // , CW_FLIP, CCW_FLIP, TWICE_FLIP;
    }

    /*public static enum Verb{
        //MOVE_NOT_USED(0), 
        LINE(2), // 1
        //QUAD_NOT_USED(0), CONIC_NOT_USED(0), 
        CUBIC(6); // 4
        //CLOSE_NOT_USED(0), DONE_NOT_USED(0);
        public final int dataPoints;
        private Verb(int dataPoints){
            this.dataPoints = dataPoints;
        }
    }*/

    public static class Segment{
        public final Verb verb;
        public final float[] data;
        public Segment(Verb verb, float[] allData, int dataOffset, boolean from102to60){
            this.verb=verb;
            this.data=new float[verb.dataPoints];
            for(int i=0;i<data.length;i++){
                data[i] = allData[dataOffset+i];
                if(from102to60)
                data[i] /= 1.7;
            }
        }
        public void toPath(Path path){
            switch(verb){
            case LINE:
                path.rLineTo(data[0],data[1]);
                break;
            case CUBIC:
                path.rCubicTo(data[0],data[1], data[2],data[3], data[4],data[5])
                break;
            }
        }
        void flip(){
            switch(verb){
            case CUBIC:
                data[3] = -data[3];
                data[5] = -data[5];
            case LINE:
                data[1] = -data[1];
            }
        }
        public Segment clone(){
            return new Segment(verb, data, 0, false);
        }
    }

    static SVGPath[][] generateAll(){
        SVGPath[][] ret = new SVGPath[3*3*2][];
        int reti=0;
        for(float neckWidth=0f; neckWidth < 13f; neckWidth += 5f){ // from narrow to wide
            for(float curvature=0f; curvature < 6.25f; curvature += 2.5f){ // from straight to curved
                ret[reti] = generate_cfirst(neckWidth, curvature);
                reti++;
                ret[reti] = generate_csecond(neckWidth, curvature);
                reti++;
            }
        }
        // TODO: add these SVGPaths to the pool from which to get ready-rotated cfirst paths with the selected neckWidth and curvature
        return ret;
    }

    static SVGPath[] generate_csecond(float neckWidth, float curvature){
        // TODO: BACKWARDS!
        SVGPath csecond_north_out = new SVGPath(new float[]{
            15f, 0f,/*<-cp1*/ 22.5f, 7.5f,/*<-cp2*/ 22.5f, 17.5f,
            0f, -10f,/*<-cp1*/ -17.5f+neckWidth, -17.5f,/*<-cp2*/ -15+neckWidth, 25,
            2.5f, 7.5f,/*<-cp1*/ 32.5f-neckWidth, 7.5f+curvature,/*<-cp2*/ 47.5-neckWidth, 7.5f+curvature,
            45f, -curvature
        });
        SVGPath csecond_north_in = new SVGPath(csecond_north_out);
        csecond_north_in.rotate(Rotate.FLIP);

        SVGPath csecond_east_out = new SVGPath(csecond_north_out);
        csecond_east_out.rotate(Rotate.CLOCKWISE);

        SVGPath csecond_west_out = new SVGPath(csecond_north_out);
        csecond_west_out.rotate(Rotate.COUNTERCLOCKWISE);
        
        SVGPath csecond_south_out = new SVGPath(csecond_north_out);
        csecond_south_out.rotate(Rotate.TWICE);
        
        SVGPath csecond_east_in = new SVGPath(csecond_north_in);
        csecond_east_in.rotate(Rotate.CLOCKWISE);

        SVGPath csecond_west_in = new SVGPath(csecond_north_in);
        csecond_west_in.rotate(Rotate.COUNTERCLOCKWISE);
        
        SVGPath csecond_south_in = new SVGPath(csecond_north_in);
        csecond_south_in.rotate(Rotate.TWICE);

        return new SVGPath[]{
            csecond_north_out, csecond_west_out, csecond_south_out, csecond_east_out, 
            csecond_north_in, csecond_west_in, csecond_south_in, csecond_east_in
        };
    }

    static SVGPath[] generate_cfirst(float neckWidth, float curvature){
        SVGPath cfirst_north_out = new SVGPath(new float[]{
            45f, curvature,
            15f, 0f,/*<-cp1*/ 45f-neckWidth, -curvature,/*<-cp2*/ 47.5f-neckWidth, -7.5f-curvature,
            2.5f, -7.5f,/*<-cp1*/ -15+neckWidth, -15f,/*<-cp2*/ -15+neckWidth, -25f,
            0f, -10f,/*<-cp1*/ 7.5f, -17.5f,/*<-cp2*/ 22.5f, -17.5f
        });
        SVGPath cfirst_north_in = new SVGPath(cfirst_north_out);
        cfirst_north_in.rotate(Rotate.FLIP);

        SVGPath cfirst_east_out = new SVGPath(cfirst_north_out);
        cfirst_east_out.rotate(Rotate.CLOCKWISE);

        SVGPath cfirst_west_out = new SVGPath(cfirst_north_out);
        cfirst_west_out.rotate(Rotate.COUNTERCLOCKWISE);
        
        SVGPath cfirst_south_out = new SVGPath(cfirst_north_out);
        cfirst_south_out.rotate(Rotate.TWICE);
        
        SVGPath cfirst_east_in = new SVGPath(cfirst_north_in);
        cfirst_east_in.rotate(Rotate.CLOCKWISE);

        SVGPath cfirst_west_in = new SVGPath(cfirst_north_in);
        cfirst_west_in.rotate(Rotate.COUNTERCLOCKWISE);
        
        SVGPath cfirst_south_in = new SVGPath(cfirst_north_in);
        cfirst_south_in.rotate(Rotate.TWICE);

        return new SVGPath[]{
            cfirst_north_out, cfirst_west_out, cfirst_south_out, cfirst_east_out, 
            cfirst_north_in, cfirst_west_in, cfirst_south_in, cfirst_east_in
        };
    }

    private ArrayList<Segment> list;
    //private int i;
    private Rotate rotate;

    public SVGPath(SVGPath copy){
        list = new ArrayList<>();
        for(Segment segment: copy.list){
            list.add(segment.clone());
        }
    }

    public SVGPath(float[] allData, Verb[] verbs){
        list = new ArrayList<>();
        int i=0;
        for(Verb v:verbs){
            list.add(new Segment(v, allData, i, true));
            i += v.dataPoints;
        }
        //i = 0;
    }

    /*public void resetTo(int i){
        this.i = i;
    }*/

    /*public boolean hasNext(){
        return i < list.size();
    }

    public Segment next(){
        if(!hasNext()) throw new NoSuchElementException();
        Segment ret = peek();
        i++;
        return ret;
    }*/

    /*public Segment peek(){
        return list.get(i);
    }*/

    /*public void remove(){
        throw new UnsupportedOperationException();
    }*/



  /*  public SVGPath rotate(Rotate rot){
        if(rotate == null){
            rotate = rot;
        }
        else{

        }
        return this;
    }

    public void doTranslate(int x, int y){
        //
    }

public void doRotate(){
    doRotate(rotate);
} */
public void doRotate(Rotate r){}
        switch(r){
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

    public SVGPath append(SVGPath nextPath){
        list.addAll(nextPath.list);
        /*nextPath.resetTo(0);
        while(nextPath.hasNext()){
            list.add(nextPath.next());
        }*/
        return this;
    }

    public Path toPath(float x, float y){
        Path ret=new Path();
        ret.moveTo(x, y);
        //i = 0;
        for(Segment segment:list){//while(hasNext()){
            /*next()*/segment.toPath(ret);
        }
        ret.close();
        return ret;
    }

}