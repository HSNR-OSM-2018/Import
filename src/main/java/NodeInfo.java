import de.hsnr.osm2018.data.graph.Node;

public class NodeInfo {

    private float _lat;
    private float _lon;
    private boolean _marked;

    public NodeInfo(double lat, double lon){
        _lat = (float) lat;
        _lon = (float) lon;
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
        return (int) Node.getDistance(this._lat, this._lon, node._lat, node._lon);
    }
}
