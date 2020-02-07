package hu.vtominator.edu2.Model;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
public class Event {
    private static final String TAG = "Event";


    private int event_id;
    private String type;
    private String main_category;
    private String side_category;
    private String eventname;
    private String date;
    private String time;
    private String location;
    private String short_description;
    private String description;
    private String picture;
    private int pinned;

    private boolean participate;
    private boolean interest;
    private boolean favorite;
    private boolean seen;
    private int emberekSzama;


    public Event(int event_id, String type, String main_category, String side_category, String eventname, String date, String time, String location, String short_description, String description, String picture, int pinned) {
        this.event_id = event_id;
        this.type = type;
        this.main_category = main_category;
        this.side_category = side_category;
        this.eventname = eventname;
        this.date = date;
        this.time = time;
        this.location = location;
        this.short_description = short_description;
        this.description = description;
        this.picture = picture;
        this.pinned = pinned;
    }


    public static long getTimeLeft(Event currentEvent) {
        long diff;

        String pattern = "yyyy-MM-dd HH:mm:ss";
        String date = currentEvent.getDate() + " " + currentEvent.getTime();


        Date today = new Date();

        try {
            DateFormat df = new SimpleDateFormat(pattern);
            Date eventDay = df.parse(date);
            diff = eventDay.getTime() - today.getTime();
            return diff;


        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static final Comparator<Event> BY_PRIOR = new Comparator<Event>() {
        @Override
        public int compare(Event event, Event o2) {
            if (event.getPinned() < o2.getPinned()) return 1;
            else if (event.getPinned() > o2.getPinned()) return -1;
            else if (getTimeLeft(event) < getTimeLeft(o2)) return -1;
            else if (getTimeLeft(event) > getTimeLeft(o2)) return 1;
            return 0;
        }
    };


    public int getEvent_id() {
        return event_id;
    }

    public String getType() {
        return type;
    }

    public String getMain_category() {
        return main_category;
    }

    public String getSide_category() {
        return side_category;
    }

    public String getEventname() {
        return eventname;
    }

    public String getShort_description() {
        return short_description;
    }

    public String getDate() {

        return date;

    }

    public String getTime() {
        return time;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public String getPicture() {
        return picture;
    }

    public int getPinned() {
        return pinned;
    }


    public boolean isParticipate() {
        return this.participate;
    }

    public void setParticipate(boolean participate) {
        this.participate = participate;
    }


    public boolean isInterest() {
        return this.interest;
    }

    public void setInterest(boolean interest) {
        this.interest = interest;
    }


    public boolean isFavorite() {
        return this.favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }


    public boolean isSeen() {
        return this.seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }


    public void setEmberekSzama(int emberekSzama) {
        this.emberekSzama = emberekSzama;
    }

    public int getEmberekSzama() {
        return emberekSzama;
    }


}