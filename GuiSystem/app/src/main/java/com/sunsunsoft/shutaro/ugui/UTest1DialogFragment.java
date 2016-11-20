package com.sunsunsoft.shutaro.ugui;

/**
 * Created by shutaro on 2016/11/20.
 */

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

import java.util.LinkedList;


interface UTest1DialogCallbacks {
    void submit(Bundle args);
    void cancel();
}

/**
 * 複数の入力項目があるダイアログ
 *
 * 全画面表示
 */
public class UTest1DialogFragment extends DialogFragment {
    /**
     * Constract
     */
    private final static String KEY_NAME = "key_name";

    private static final int[] editIds = {
            R.id.editText,
            R.id.editText2,
            R.id.editText3,
            R.id.editText4,
            R.id.editText5
    };

    // key names
    public static final String KEY_RETS = "key_rets";
    public static final String KEY_TEXTS = "key_texts";

    /**
     * Member variables
     */
    private UTest1DialogCallbacks dialogCallbacks;
    private LinkedList<EditText> edits = new LinkedList<>();
    private String[] argTexts;

    /**
     * Get/Set
     */

    /**
     * Constructor
     */
    static UTest1DialogFragment createInstance(UTest1DialogCallbacks callbacks, String[]
            texts) {
        UTest1DialogFragment dialog = new UTest1DialogFragment();

        dialog.dialogCallbacks = callbacks;

        // set arguments
        Bundle args = new Bundle();
        args.putStringArray(KEY_TEXTS, texts);
        dialog.setArguments(args);

        return dialog;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 引数を取得
        argTexts = getArguments().getStringArray(KEY_TEXTS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        return inflater.inflate(R.layout.edit_dialog_texts, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        for (int id : editIds) {
            EditText edit = (EditText)view.findViewById(id);
            if (edit != null) {
                edits.add(edit);
            }
        }
        if (argTexts.length >= edits.size()) {
            for (int i=0; i<edits.size(); i++) {
                edits.get(i).setText(argTexts[i]);
            }
        }

        // Listener
        (view.findViewById(R.id.buttonOK)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                submit();
            }
        });

        (view.findViewById(R.id.buttonCancel)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cancel();
            }
        });

        setStyle(STYLE_NORMAL, android.R.style.Theme);
    }

    /**
     * 呼び出し元に引数を返して終了
     */
    private void submit() {
        Bundle args = new Bundle();

        LinkedList<String> textLists = new LinkedList<>();
        for (EditText edit : edits) {
            textLists.add(edit.getText().toString());
        }

        args.putStringArray(KEY_RETS, textLists.toArray(new String[0]));

        if (dialogCallbacks != null) {
            if (args != null) {
                dialogCallbacks.submit(args);
            }
        }

        dismiss();
    }

    /**
     * キャンセルしたときの処理
     */
    private void cancel() {
        if (dialogCallbacks != null) {
            dialogCallbacks.cancel();
        }
        dismiss();
    }

    /**
     *
     * @param dialog
     */
    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        cancel();
    }
}