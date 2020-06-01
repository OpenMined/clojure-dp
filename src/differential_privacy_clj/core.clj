(ns differential-privacy-clj.core
  (:import [com.google.privacy.differentialprivacy
            BoundedSum Count LaplaceNoise GaussianNoise]
           [com.google.protobuf
            InvalidProtocolBufferException])
  (:refer-clojure :rename {count size}))  ;; "count" is used for private count in this ns

(def default-data-chunk-size
  ;; It makes sense to have a multiplication of 32 here.
  ;; Lazy sequences in Clojure realize elements ahead of time in groups
  ;; (chunks) of size 32.
  32000)

(defn bounded-sum [data-seq
                   & {:keys [lower upper max-partitions epsilon chunk-size]
                      :or {chunk-size default-data-chunk-size}}]
  {:pre [lower upper max-partitions epsilon]}
  (let [bsm (-> (BoundedSum/builder)
                (.lower lower)
                (.upper upper)
                (.maxPartitionsContributed max-partitions)
                (.epsilon epsilon)
                .build)]
    (doseq [chunk (partition-all chunk-size data-seq)]
      (.addEntries bsm chunk))
    (.computeResult bsm)))


(defn count [data-seq & {:keys [max-partitions epsilon]}]
  {:pre [max-partitions epsilon]}
  (let [cnt (-> (Count/builder)
                (.maxPartitionsContributed max-partitions)
                (.epsilon epsilon)
                .build)]
    (.incrementBy cnt (size data-seq))
    (.computeResult cnt)))


(defn laplace-noise [] (LaplaceNoise.))

(defn gaussian-noise [] (GaussianNoise.))
