import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;


public class Game {

  private final String roomName;
  private final boolean hasPassword;
  private final String password;
  private ConcurrentHashMap<Integer, Farm> farms;
  private long maxPlayers;
  private RoundManager roundManager;

  private class RoundManager{
    private boolean contracts;
    private boolean management;

    public RoundManager(boolean contracts, boolean management){
      this.contracts = contracts;
      this.management = management;
    }
  }


  public Game(String name, long l) {
    roomName = name;
    farms = new ConcurrentHashMap<>();
    hasPassword = false;
    password = "";
    roundManager = new RoundManager(false, false);
    this.maxPlayers = l;
    // TODO Auto-generated constructor stub
  }

  public Game(String name, String pass, int maxPlayers) {
    roomName = name;
    farms = new ConcurrentHashMap<>();
    hasPassword = true;
    password = pass;
    this.maxPlayers = maxPlayers;
    // TODO Auto-generated constructor stub
  }

  public String getRoomName() {
    return roomName;
  }

  public boolean hasFarmer(String name) {
    // TODO Auto-generated method stub
    return farms.get(name)!=null;
  }

  /*public void addFarmer(String newPlayer) {
    // TODO Auto-generated method stub
    farms.put(newPlayer, new Farm(newPlayer, 1000));
  }*/

  public void addFarmer(String newPlayer, int clientID) {
    // TODO Auto-generated method stub
    Farm f = new Farm(newPlayer, 1000);
    f.setClientID(clientID);
    f.getFields()[0] = new Field();
    f.getFields()[1] = new Field();
    farms.put(clientID, f);

  }

  public Boolean hasPassword(){
    return hasPassword;
  }

  public String getPassword(){
    return password;
  }

  public long getMaxPlayers() {
    return maxPlayers;
  }

  public boolean isContracts() {
    return roundManager.contracts;
  }

  public void setContracts(boolean contracts) {
    roundManager.contracts = contracts;
  }

  public boolean isManagement() {
    return roundManager.management;
  }

  public void setManagement(boolean management) {
    roundManager.management = management;
  }

  public boolean isFull(){
    return(farms.size() >= maxPlayers);
  }

  public void setField(int clientID, int field, Crop crop){
    farms.get(clientID).getFields()[field].setCrop(crop);
  }

  public ArrayList<Crop> getFieldsFor(Integer clientID) {
    // TODO Auto-generated method stub
    ArrayList<Crop> cropList = new ArrayList<>();
    for(Field f:farms.get(clientID).getFields()){
      cropList.add(f.getCrop());
    }
    return cropList;
  }


}
