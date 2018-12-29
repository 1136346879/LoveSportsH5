package cmccsi.mhealth.portal.sports.bean;

public class GoalNetInfo extends BaseNetItem{
    public GoalBaseInfo goalinfo;
    
    public GoalNetInfo(){
        goalinfo = new GoalBaseInfo();
    }

    @Override
    public void setValue(BaseNetItem bni) {
        GoalNetInfo info = (GoalNetInfo)bni;
        this.goalinfo = info.goalinfo;
    }

    @Override
    public boolean isValueData(BaseNetItem bni) {
        return true;
    }

}
