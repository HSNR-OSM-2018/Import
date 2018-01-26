public class NodeInfo {

    private double _lat;
    private double _lon;
    private boolean _marked;

    public NodeInfo(double lat, double lon){
        _lat = lat;
        _lon = lon;
        _marked = false;
    }

    public double Lat() {
        return _lat;
    }

    public double Lon() {
        return _lon;
    }

    public boolean isMarked() {
        return _marked;
    }

    public void setMarked(boolean _marked) {
        this._marked = _marked;
    }

    public int distToNode(NodeInfo node){
        double dist = 0;
        double lat = 111.3*(this._lat - node.Lat());
        double lon = 71.5*(this._lon - node.Lon());
        dist = Math.sqrt(lat*lat+lon*lon);
        return (int)(dist*1000);
    }
}
