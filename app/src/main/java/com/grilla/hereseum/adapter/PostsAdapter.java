package com.grilla.hereseum.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.grilla.hereseum.InstaPost;
import com.grilla.hereseum.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by bill on 8/7/15.
 */
public class PostsAdapter extends BaseAdapter {
    private static final String TAG = "PostsAdapter";

    private Context mContext;

    private List<InstaPost> mPosts;

    private int mAvatarSize;
    private int mMonth;

    public PostsAdapter(Context context) {
        mContext = context;
        mPosts = new ArrayList<>();

        mAvatarSize = (int)context.getResources().getDimension(R.dimen.avatar_size);
    }

    public void setPosts(List<InstaPost> posts, int month) {
        mMonth = month;
        mPosts = posts;
        notifyDataSetChanged();
    }

    public void addPosts(final List<InstaPost> posts) {
        Task.callInBackground(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                for (int i = 0, l = posts.size(); i < l; i++) {
                    InstaPost post = posts.get(i);
                    if (!contains(post) && post.getMonth() == mMonth) {
                        mPosts.add(posts.get(i));
                    }
                }
                return null;
            }
        }).continueWith(new Continuation<Void, Void>() {
            @Override
            public Void then(Task<Void> task) throws Exception {
                notifyDataSetChanged();
                return null;
            }
        }, Task.UI_THREAD_EXECUTOR);
    }

    private boolean contains(InstaPost post) {
        for (int i = 0, l = mPosts.size(); i < l; i++) {
            if (mPosts.get(i).getLink().equals(post.getLink())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int getCount() {
        return mPosts != null ? mPosts.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return mPosts != null ? mPosts.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public long getLastTime() {
        if (mPosts == null || mPosts.isEmpty()) {
            return 0;
        }

        return mPosts.get(mPosts.size()-1).getCreatedTime();
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
        final InstaPost post = mPosts.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.view_post, parent, false);
        }

        if (convertView.getTag() == null) {
            convertView.setTag(new PostListener(Uri.parse(post.getLink())));
        } else {
            ((PostListener)convertView.getTag()).setLink(Uri.parse(post.getLink()));
        }

        TextView.class.cast(convertView.findViewById(R.id.post_user)).setText(post.getUser());
        TextView.class.cast(convertView.findViewById(R.id.post_date)).setText(post.getCreated());

        ImageView image = (ImageView)convertView.findViewById(R.id.post_image);
        Picasso.with(mContext).load(post.getImageUrl()).placeholder(R.drawable.loading_bg).into(image);
        image.setOnLongClickListener((PostListener)convertView.getTag());

        ImageView avatar = (ImageView)convertView.findViewById(R.id.post_avatar);
        Picasso.with(mContext).load(post.getAvatarUrl()).resize(mAvatarSize, mAvatarSize).centerInside().into(avatar);

        return convertView;
    }

    private class PostListener implements View.OnLongClickListener {
        private Uri mLink;

        public PostListener(Uri link) {
            mLink = link;
        }

        public void setLink(Uri link) {
            mLink = link;
        }

        @Override
        public boolean onLongClick(View v) {
            Intent i = new Intent(Intent.ACTION_VIEW, mLink);
            mContext.startActivity(i);
            return true;
        }
    }
}
