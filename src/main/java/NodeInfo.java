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
        int dist = 0;
        double lat = this._lat - node.Lat();
        double lon = this._lon - node.Lon();
        dist = (int)Math.sqrt(Math.pow(lat,2) + Math.pow(lon,2));
        return dist;
    }
}
