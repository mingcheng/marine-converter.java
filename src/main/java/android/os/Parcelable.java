package android.os;

/**
 * Created with IntelliJ IDEA.
 * <p/>
 * User: mingcheng
 * Date: 15/12/17
 */
public interface Parcelable {
    int PARCELABLE_WRITE_RETURN_VALUE = 1;
    int CONTENTS_FILE_DESCRIPTOR = 1;

    int describeContents();

    void writeToParcel(Object var1, int var2);

    public interface Creator<T> {
        T createFromParcel(Object var1);

        T[] newArray(int var1);
    }
}
