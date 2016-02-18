/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.volley.toolbox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * ByteArrayPool is a source(本源) and repository(仓库) of <code>byte[]</code> objects. Its purpose is to
 * supply(补充) those buffers(缓冲区) to consumers who need to use them for a short period of time and then
 * dispose(处理) of them. Simply(简单的) creating and disposing such buffers in the conventional manner can
 * considerable(考虑的) heap churn(堆抖动) and garbage collection delays(垃圾回收延迟) on Android, which lacks(缺乏) good management of
 * short-lived(短暂的) heap objects. It may be advantageous(有利的) to trade off(此消彼涨) some memory in the form of(用...的形式) a
 * permanently(永久的) allocated pool of buffers in order to gain(获得) heap performance improvements; that is
 * what this class does.
 * <p>
 * A good candidate(候选) user for this class is something like an I/O system that uses large temporary(临时的)
 * <code>byte[]</code> buffers to copy data around(在周围). In these use cases(用例中), often the consumer wants
 * the buffer to be a certain(一定) minimum size to ensure good performance (e.g. when copying data chunks
 * off of a stream), but doesn't mind if the buffer is larger than the minimum. Taking this into
 * account and also to maximize the odds of being able to reuse a recycled buffer, this class is
 * free to return buffers larger than the requested size. The caller needs to be able to gracefully
 * deal with getting buffers any size over the minimum.
 * <p>
 * If there is not a suitably-sized buffer in its recycling pool when a buffer is requested, this
 * class will allocate a new buffer and return it.
 * <p>
 * This class has no special ownership(所有权) of buffers it creates; the caller is free to take a buffer
 * it receives from this pool, use it permanently(永久的), and never return it to the pool; additionally(此外),
 * it is not harmful to return to this pool a buffer that was allocated elsewhere, provided there
 * are no other lingering references to it.
 * <p>
 * This class ensures that the total size of the buffers in its recycling pool never exceeds(超过) a
 * certain byte limit. When a buffer is returned that would cause the pool to exceed the limit,
 * least-recently-used(最近最少使用) buffers are disposed.
 */
public class ByteArrayPool {
    /** The buffer pool, arranged both by last use and by buffer size */
	// ArrayList基于动态数组--Set和Get操作，ArrayList优于LinkedList
	// LinkedList基于链表--Add和Remove，LinkedList比较占优势
    private List<byte[]> mBuffersByLastUse = new LinkedList<byte[]>();
    private List<byte[]> mBuffersBySize = new ArrayList<byte[]>(64);

    /** The total size of the buffers in the pool */
    private int mCurrentSize = 0;

    /**
     * The maximum aggregate(聚合) size of the buffers in the pool. Old buffers are discarded(丢弃) to stay
     * under this limit.
     */
    private final int mSizeLimit;

    /** Compares buffers by size */
    protected static final Comparator<byte[]> BUF_COMPARATOR = new Comparator<byte[]>() {
        @Override
        public int compare(byte[] lhs, byte[] rhs) {
            return lhs.length - rhs.length;
        }
    };

    /**
     * @param sizeLimit the maximum size of the pool, in bytes
     */
    public ByteArrayPool(int sizeLimit) {
        mSizeLimit = sizeLimit;
    }

    /**
     * Returns a buffer from the pool if one is available in the requested size, or allocates a new
     * one if a pooled one is not available.
     *
     * @param len the minimum size, in bytes, of the requested buffer. The returned buffer may be
     *        larger.
     * @return a byte[] buffer is always returned.
     */
    public synchronized byte[] getBuf(int len) {
        for (int i = 0; i < mBuffersBySize.size(); i++) {
            byte[] buf = mBuffersBySize.get(i);
            if (buf.length >= len) {
                mCurrentSize -= buf.length;
                mBuffersBySize.remove(i);
                mBuffersByLastUse.remove(buf);
                return buf;
            }
        }
        return new byte[len];
    }

    /**
     * Returns a buffer to the pool, throwing away(丢弃) old buffers if the pool would exceed its allotted
     * size.
     *
     * @param buf the buffer to return to the pool.
     */
    public synchronized void returnBuf(byte[] buf) {
        if (buf == null || buf.length > mSizeLimit) {
            return;
        }
        
        mBuffersByLastUse.add(buf);
        // 对于List进行排序
        int pos = Collections.binarySearch(mBuffersBySize, buf, BUF_COMPARATOR);
        if (pos < 0) {
            pos = -pos - 1;
        }
        
        mBuffersBySize.add(pos, buf);
        mCurrentSize += buf.length;
        trim();
    }

    /**
     * Removes buffers from the pool until it is under its size limit.
     */
    // trim -- 整齐的
    private synchronized void trim() {
        while (mCurrentSize > mSizeLimit) {
            byte[] buf = mBuffersByLastUse.remove(0);
            mBuffersBySize.remove(buf);
            mCurrentSize -= buf.length;
        }
    }

}
