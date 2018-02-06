package Merger;

import crosby.binary.osmosis.OsmosisSerializer;
import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.container.v0_6.NodeContainer;
import org.openstreetmap.osmosis.core.container.v0_6.WayContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import org.openstreetmap.osmosis.osmbinary.file.BlockOutputStream;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EggReader implements Sink {

    private MergeWriter _writer;
    private Set<Long> _bielefeld;

    public EggReader(String target){
        _writer = new MergeWriter();
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(target);
            _writer.setSink(new OsmosisSerializer(new BlockOutputStream(outputStream)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        initBielefeld();
    }

    @Override
    public void process(EntityContainer entityContainer) {
        if(entityContainer instanceof NodeContainer){
            Node node = ((NodeContainer) entityContainer).getEntity();
            _writer.writeNode(node.getId(),node.getLatitude(),node.getLongitude());
            node = null;
        } else if(entityContainer instanceof WayContainer){

            Way way = ((WayContainer) entityContainer).getEntity();
            if(!bielefeld(way.getId())) {
                _writer.writeWay(way.getId(), way.getTags(), way.getWayNodes());
            } else {
                System.out.println("part bielefeld");
            }
            way = null;
        }
    }

    @Override
    public void initialize(Map<String, Object> map) {

    }

    @Override
    public void complete() {

    }

    @Override
    public void release() {

    }

    private boolean bielefeld(long id){
        if(_bielefeld.contains(id))
            return true;
        else
            return false;
    }

    private void initBielefeld(){
        _bielefeld = new HashSet<>();
        _bielefeld.add(189128418l);
        _bielefeld.add(185273093l);
        _bielefeld.add(39321632l);
        _bielefeld.add(27593441l);
        _bielefeld.add(40162696l);
        _bielefeld.add(40028046l);
        _bielefeld.add(40028032l);
        _bielefeld.add(59726038l);
        _bielefeld.add(273290563l);
        _bielefeld.add(27402750l);
        _bielefeld.add(177990356l);
        _bielefeld.add(177994870l);
        _bielefeld.add(36006944l);
        _bielefeld.add(36006420l);
        _bielefeld.add(35912178l);
        _bielefeld.add(41200775l);
        _bielefeld.add(341067581l);
        _bielefeld.add(194224323l);
        _bielefeld.add(43302749l);
        _bielefeld.add(177977852l);
        _bielefeld.add(258509311l);
        _bielefeld.add(30445384l);
        _bielefeld.add(154937639l);
        _bielefeld.add(30003854l);
        _bielefeld.add(225528899l);
        _bielefeld.add(163886832l);
        _bielefeld.add(163886835l);
        _bielefeld.add(163886834l);
        _bielefeld.add(29091346l);
        _bielefeld.add(225710225l);
        _bielefeld.add(26541625l);
        _bielefeld.add(24383021l);
        _bielefeld.add(208157040l);
        _bielefeld.add(35270556l);
        _bielefeld.add(27178830l);
        _bielefeld.add(208080773l);
        _bielefeld.add(92740424l);
        _bielefeld.add(331072899l);
        _bielefeld.add(163349633l);
        _bielefeld.add(37332175l);
        _bielefeld.add(360016201l);
        _bielefeld.add(360195066l);
        _bielefeld.add(29293291l);
        _bielefeld.add(32644163l);
        _bielefeld.add(145488336l);
        _bielefeld.add(73838674l);
        _bielefeld.add(177962690l);
        _bielefeld.add(25931357l);
        _bielefeld.add(323484391l);
        _bielefeld.add(205604155l);
        _bielefeld.add(36909295l);
        _bielefeld.add(32950280l);
        _bielefeld.add(172607902l);
        _bielefeld.add(187893007l);
        _bielefeld.add(245498663l);
        _bielefeld.add(24950250l);
        _bielefeld.add(172336964l);
        _bielefeld.add(36878194l);
        _bielefeld.add(35920370l);
        _bielefeld.add(383996312l);
        _bielefeld.add(34924872l);
        _bielefeld.add(168658380l);
        _bielefeld.add(285293819l);
        _bielefeld.add(197456531l);
        _bielefeld.add(177358871l);
        _bielefeld.add(163354413l);
        _bielefeld.add(82244973l);
        _bielefeld.add(24150511l);
        _bielefeld.add(258224867l);
        _bielefeld.add(24150384l);
        _bielefeld.add(163060998l);
        _bielefeld.add(27874534l);
        _bielefeld.add(215840518l);
        _bielefeld.add(145488365l);
        _bielefeld.add(333180005l);
        _bielefeld.add(27336170l);
        _bielefeld.add(37649694l);
        _bielefeld.add(175936309l);
        _bielefeld.add(35414805l);
        _bielefeld.add(33881981l);
        _bielefeld.add(158933822l);
        _bielefeld.add(107490366l);
        _bielefeld.add(290002281l);
        _bielefeld.add(40032798l);
        _bielefeld.add(38564487l);
        _bielefeld.add(32913080l);
        _bielefeld.add(91152764l);
        _bielefeld.add(177066957l);
        _bielefeld.add(40816090l);
        _bielefeld.add(25013188l);
        _bielefeld.add(26633437l);
        _bielefeld.add(40521305l);
        _bielefeld.add(26125075l);
        _bielefeld.add(28944720l);
        _bielefeld.add(62085317l);
        _bielefeld.add(189128388l);
        _bielefeld.add(53040567l);
        _bielefeld.add(189002408l);
        _bielefeld.add(154937639l);
        _bielefeld.add(189128396l);
        _bielefeld.add(263150899l);
        //-----------------------------------
        _bielefeld.add(323261644l);
        _bielefeld.add(32735754l);
        _bielefeld.add(32735753l);
        _bielefeld.add(323261645l);
        _bielefeld.add(246051220l);
        _bielefeld.add(246051221l);
        _bielefeld.add(293724615l);
        _bielefeld.add(293720224l);
        _bielefeld.add(293724617l);
        _bielefeld.add(27161404l);
        _bielefeld.add(293724614l);
        _bielefeld.add(293724607l);
        _bielefeld.add(275857459l);
        _bielefeld.add(4473456l);
        _bielefeld.add(48837255l);
        _bielefeld.add(119260620l);
        _bielefeld.add(267133905l);
        _bielefeld.add(170079432l);
        _bielefeld.add(322768191l);
        _bielefeld.add(322768191l);
        _bielefeld.add(267133904l);
        _bielefeld.add(114423555l);
        _bielefeld.add(396680866l);
        _bielefeld.add(41430076l);
        _bielefeld.add(497239313l);
        _bielefeld.add(285804777l);
        _bielefeld.add(175936306l);
        _bielefeld.add(28937405l);
        _bielefeld.add(177080695l);
        _bielefeld.add(36830273l);
        _bielefeld.add(383302691l);
        _bielefeld.add(216863369l);
    }
}
