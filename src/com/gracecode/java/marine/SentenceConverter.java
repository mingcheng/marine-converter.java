package com.gracecode.java.marine;

import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.model.LatLng;
import net.sf.marineapi.nmea.event.SentenceEvent;
import net.sf.marineapi.nmea.event.SentenceListener;
import net.sf.marineapi.nmea.io.SentenceReader;
import net.sf.marineapi.nmea.parser.DataNotAvailableException;
import net.sf.marineapi.nmea.sentence.RMCSentence;
import net.sf.marineapi.nmea.sentence.SentenceId;
import net.sf.marineapi.nmea.util.Position;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * <p/>
 * User: mingcheng
 * Date: 15/12/15
 *
 * @see "http://baike.baidu.com/item/NMEA"
 */
public class SentenceConverter implements SentenceListener {
    private final SentenceReader sentenceReader;
    private final FileOutputStream fileOutputStream;
    private final JSONArray locationsJsonArray;
    private final JSONObject jsonObject;
    private final File outputFile;
    private float distance = 0;
    private Position lastRecordedLocation;
    private SimpleDateFormat simpleDateFormatter;
    private double topSpeed = 0;
    private double averageSpeed = 0;

    public SentenceConverter(File inputFile, File outputFile) throws IOException {
        if (outputFile.exists() && outputFile.delete()) {
            System.out.println(outputFile.getAbsoluteFile() + " is exists, deleted.");
        }

        this.outputFile = outputFile;
        fileOutputStream = new FileOutputStream(outputFile);
        sentenceReader = new SentenceReader(new FileInputStream(inputFile));

        locationsJsonArray = new JSONArray();
        jsonObject = new JSONObject();

        sentenceReader.addSentenceListener(this, SentenceId.RMC);
        sentenceReader.setInterval(0);
    }

    public void start() {
        sentenceReader.start();
    }

    @Override
    public void readingPaused() {
        jsonObject.put("data", locationsJsonArray);
        jsonObject.put("total", locationsJsonArray.length());
        jsonObject.put("distance", Math.round(distance * 100) / 100.0);
        jsonObject.put("topSpeed", topSpeed);
        jsonObject.put("averageSpeed", averageSpeed / locationsJsonArray.length());

        try {
            fileOutputStream.write(jsonObject.toString().getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            sentenceReader.stop();
        }
    }

    @Override
    public void readingStarted() {
        // ...
    }

    @Override
    public void readingStopped() {
        System.out.println("Convert " + outputFile.getAbsolutePath() + " is finished");
    }

    @Override
    public void sentenceRead(SentenceEvent sentenceEvent) {
        try {
            RMCSentence sentence = (RMCSentence) sentenceEvent.getSentence();
            Position position = sentence.getPosition();

            LatLng location = getConvertedLocation(new LatLng(position.getLatitude(), position.getLongitude()));
            if (lastRecordedLocation != null) {
                distance += position.distanceTo(lastRecordedLocation);
            }
            lastRecordedLocation = position;

            JSONObject item = new JSONObject();
            item.put("longitude", location.longitude);
            item.put("latitude", location.latitude);
            item.put("speed", sentence.getSpeed()); // 1knots = 1.852 km/h
            item.put("date", parseDate(sentence));
            item.put("course", sentence.getCourse());

            if (topSpeed <= sentence.getSpeed()) {
                topSpeed = sentence.getSpeed();
            }
            averageSpeed += sentence.getSpeed();

            locationsJsonArray.put(item);
//            locationsJsonArray.put(new double[]{location.longitude, location.latitude});
        } catch (DataNotAvailableException e) {
//            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    private Date parseDate(RMCSentence s) throws ParseException, IndexOutOfBoundsException {
        if (simpleDateFormatter == null) {
            simpleDateFormatter = new SimpleDateFormat("ddMMyy HHmmss.SSS Z");
        }

        return simpleDateFormatter.parse(s.getDate() + " " + s.getTime() + " +0000");
    }

    /**
     * 基于高德地图的偏移修正
     *
     * @param latLng
     * @return
     */
    private LatLng getConvertedLocation(LatLng latLng) {
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        converter.coord(latLng);
        return converter.convert();
    }
}
