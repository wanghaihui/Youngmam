package com.xiaobukuaipao.youngmam.emoji;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.xiaobukuaipao.youngmam.R;

/**
 * Created by xiaobu1 on 15-8-10.
 */
public class EmojiGridFragment extends BaseEmojiFragment implements AdapterView.OnItemClickListener {

    private Emoji[] mData;

    protected static EmojiGridFragment newInstance(Emoji[] emojis) {
        EmojiGridFragment emojiGridFragment = new EmojiGridFragment();
        Bundle args = new Bundle();
        args.putSerializable("emojis", emojis);
        emojiGridFragment.setArguments(args);
        return emojiGridFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.emoji_grid, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GridView gridView = (GridView) view.findViewById(R.id.emoji_gridview);
        mData = (Emoji[]) getArguments().getSerializable("emojis");
        gridView.setAdapter(new EmojiAdapter(view.getContext(), mData));
        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("emojis", mData);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnEmojiClickedListener) {
            onEmojiClickedListener = (OnEmojiClickedListener) activity;
        } else {
            throw new IllegalArgumentException(activity + "must implement interface " + OnEmojiClickedListener.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        onEmojiClickedListener = null;
        super.onDetach();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (onEmojiClickedListener != null) {
            onEmojiClickedListener.onEmojiClicked((Emoji) parent.getItemAtPosition(position));
        }
    }


}
