package com.lzq.dawn.util.log;

import android.content.ClipData;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;

import com.lzq.dawn.DawnBridge;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * @Name :LogFormatter
 * @Time :2022/8/2 15:24
 * @Author :  Lzq
 * @Desc :
 */
public final class LogFormatter {
    static String object2String(Object object) {
        return object2String(object, -1);
    }

    static String object2String(Object object, int type) {
        if (object.getClass().isArray()) {
            return array2String(object);
        }
        if (object instanceof Throwable) {
            return DawnBridge.getFullStackTrace((Throwable) object);
        }
        if (object instanceof Bundle) {
            return bundle2String((Bundle) object);
        }
        if (object instanceof Intent) {
            return intent2String((Intent) object);
        }
        if (type == LogUtils.JSON) {
            return object2Json(object);
        } else if (type == LogUtils.XML) {
            return formatXml(object.toString());
        }
        return object.toString();
    }

    private static String bundle2String(Bundle bundle) {
        Iterator<String> iterator = bundle.keySet().iterator();
        if (!iterator.hasNext()) {
            return "Bundle {}";
        }
        StringBuilder sb = new StringBuilder(128);
        sb.append("Bundle { ");
        for (; ; ) {
            String key = iterator.next();
            Object value = bundle.get(key);
            sb.append(key).append('=');
            if (value instanceof Bundle) {
                sb.append(value == bundle ? "(this Bundle)" : bundle2String((Bundle) value));
            } else {
                sb.append(LogUtils.formatObject(value));
            }
            if (!iterator.hasNext()) {
                return sb.append(" }").toString();
            }
            sb.append(',').append(' ');
        }
    }

    private static String intent2String(Intent intent) {
        StringBuilder sb = new StringBuilder(128);
        sb.append("Intent { ");
        boolean first = true;
        String mAction = intent.getAction();
        if (mAction != null) {
            sb.append("act=").append(mAction);
            first = false;
        }
        Set<String> mCategories = intent.getCategories();
        if (mCategories != null) {
            if (!first) {
                sb.append(' ');
            }
            first = false;
            sb.append("cat=[");
            boolean firstCategory = true;
            for (String c : mCategories) {
                if (!firstCategory) {
                    sb.append(',');
                }
                sb.append(c);
                firstCategory = false;
            }
            sb.append("]");
        }
        Uri mData = intent.getData();
        if (mData != null) {
            if (!first) {
                sb.append(' ');
            }
            first = false;
            sb.append("dat=").append(mData);
        }
        String mType = intent.getType();
        if (mType != null) {
            if (!first) {
                sb.append(' ');
            }
            first = false;
            sb.append("typ=").append(mType);
        }
        int mFlags = intent.getFlags();
        if (mFlags != 0) {
            if (!first) {
                sb.append(' ');
            }
            first = false;
            sb.append("flg=0x").append(Integer.toHexString(mFlags));
        }
        String mPackage = intent.getPackage();
        if (mPackage != null) {
            if (!first) {
                sb.append(' ');
            }
            first = false;
            sb.append("pkg=").append(mPackage);
        }
        ComponentName mComponent = intent.getComponent();
        if (mComponent != null) {
            if (!first) {
                sb.append(' ');
            }
            first = false;
            sb.append("cmp=").append(mComponent.flattenToShortString());
        }
        Rect mSourceBounds = intent.getSourceBounds();
        if (mSourceBounds != null) {
            if (!first) {
                sb.append(' ');
            }
            first = false;
            sb.append("bnds=").append(mSourceBounds.toShortString());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ClipData mClipData = intent.getClipData();
            if (mClipData != null) {
                if (!first) {
                    sb.append(' ');
                }
                first = false;
                clipData2String(mClipData, sb);
            }
        }
        Bundle mExtras = intent.getExtras();
        if (mExtras != null) {
            if (!first) {
                sb.append(' ');
            }
            first = false;
            sb.append("extras={");
            sb.append(bundle2String(mExtras));
            sb.append('}');
        }
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            Intent mSelector = intent.getSelector();
            if (mSelector != null) {
                if (!first) {
                    sb.append(' ');
                }
                first = false;
                sb.append("sel={");
                sb.append(mSelector == intent ? "(this Intent)" : intent2String(mSelector));
                sb.append("}");
            }
        }
        sb.append(" }");
        return sb.toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private static void clipData2String(ClipData clipData, StringBuilder sb) {
        ClipData.Item item = clipData.getItemAt(0);
        if (item == null) {
            sb.append("ClipData.Item {}");
            return;
        }
        sb.append("ClipData.Item { ");
        String mHtmlText = item.getHtmlText();
        if (mHtmlText != null) {
            sb.append("H:");
            sb.append(mHtmlText);
            sb.append("}");
            return;
        }
        CharSequence mText = item.getText();
        if (mText != null) {
            sb.append("T:");
            sb.append(mText);
            sb.append("}");
            return;
        }
        Uri uri = item.getUri();
        if (uri != null) {
            sb.append("U:").append(uri);
            sb.append("}");
            return;
        }
        Intent intent = item.getIntent();
        if (intent != null) {
            sb.append("I:");
            sb.append(intent2String(intent));
            sb.append("}");
            return;
        }
        sb.append("NULL");
        sb.append("}");
    }

    private static String object2Json(Object object) {
        if (object instanceof CharSequence) {
            return DawnBridge.formatJson(object.toString());
        }
        try {
            return DawnBridge.getGson4LogUtils().toJson(object);
        } catch (Throwable t) {
            return object.toString();
        }
    }

    private static String formatJson(String json) {
        try {
            for (int i = 0, len = json.length(); i < len; i++) {
                char c = json.charAt(i);
                if (c == '{') {
                    return new JSONObject(json).toString(2);
                } else if (c == '[') {
                    return new JSONArray(json).toString(2);
                } else if (!Character.isWhitespace(c)) {
                    return json;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    private static String formatXml(String xml) {
        try {
            Source xmlInput = new StreamSource(new StringReader(xml));
            StreamResult xmlOutput = new StreamResult(new StringWriter());
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(xmlInput, xmlOutput);
            xml = xmlOutput.getWriter().toString().replaceFirst(">", ">" + LogUtils.LINE_SEP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xml;
    }

    private static String array2String(Object object) {
        if (object instanceof Object[]) {
            return Arrays.deepToString((Object[]) object);
        } else if (object instanceof boolean[]) {
            return Arrays.toString((boolean[]) object);
        } else if (object instanceof byte[]) {
            return Arrays.toString((byte[]) object);
        } else if (object instanceof char[]) {
            return Arrays.toString((char[]) object);
        } else if (object instanceof double[]) {
            return Arrays.toString((double[]) object);
        } else if (object instanceof float[]) {
            return Arrays.toString((float[]) object);
        } else if (object instanceof int[]) {
            return Arrays.toString((int[]) object);
        } else if (object instanceof long[]) {
            return Arrays.toString((long[]) object);
        } else if (object instanceof short[]) {
            return Arrays.toString((short[]) object);
        }
        throw new IllegalArgumentException("Array has incompatible type: " + object.getClass());
    }
}
