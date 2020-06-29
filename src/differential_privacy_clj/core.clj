(ns differential-privacy-clj.core
  (:import [com.google.privacy.differentialprivacy
            BoundedMean BoundedSum Count LaplaceNoise GaussianNoise]
           [com.google.protobuf
            InvalidProtocolBufferException])
  (:refer-clojure :rename {count size}))  ;; "count" is used for private count in this ns


(def default-data-chunk-size
  ;; It makes sense to have a multiplication of 32 here.
  ;; Lazy sequences in Clojure realize elements ahead of time in groups
  ;; (chunks) of size 32.
  32000)

(def optional-kwargs
  #{:chunk-size
    :delta
    :noise})

(def bounded-sum-required-kwargs
  #{:lower
    :upper
    :max-partitions-contributed
    :epsilon})

(def bounded-mean-required-kwargs
  #{:lower
    :upper
    :max-partitions-contributed
    :max-contributions-per-partition
    :epsilon})

(def count-required-kwargs
  #{:max-partitions-contributed
    :epsilon})


(defn validate-keyword-arguments [passed-kwargs required-kwargs]
  (let [missing-kwargs (remove (into optional-kwargs passed-kwargs)
                               required-kwargs)
        extra-kwargs (remove (into optional-kwargs required-kwargs)
                             passed-kwargs)]
    (when (not-empty missing-kwargs)
      (throw (IllegalArgumentException.
              (str "Missing keyword arguments: "
                   (clojure.string/join ", " missing-kwargs)))))
    (when (not-empty extra-kwargs)
      (throw (IllegalArgumentException.
              (str "Unexpected keyword arguments: "
                   (clojure.string/join ", " extra-kwargs)))))))


(defn bounded-algorithm [data-seq
                         algo-builder
                         required-kwargs
                         & {:keys [lower upper max-partitions-contributed
                                   max-contributions-per-partition
                                   epsilon delta noise chunk-size]
                            :or {chunk-size default-data-chunk-size}
                            :as kwargs}]
  (validate-keyword-arguments (keys kwargs)
                              required-kwargs)
  (let [algo (-> algo-builder
                 (.lower lower)
                 (.upper upper)
                 (.maxPartitionsContributed max-partitions-contributed)
                 (.epsilon epsilon)
                 (cond->
                  delta (.delta delta)
                  max-contributions-per-partition (.maxContributionsPerPartition
                                                   max-contributions-per-partition)
                  noise (.noise noise))
                 .build)]
    (doseq [data-chunk (partition-all chunk-size data-seq)]
      (.addEntries algo data-chunk))
    (.computeResult algo)))


(defn bounded-sum [data-seq & args]
  (apply bounded-algorithm
         data-seq
         (BoundedSum/builder)
         bounded-sum-required-kwargs
         args))


(defn bounded-mean [data-seq & args]
  (apply bounded-algorithm
         data-seq
         (BoundedMean/builder)
         bounded-mean-required-kwargs
         args))


(defn count [data-seq-or-cnt
             & {:keys [max-partitions-contributed epsilon delta noise] :as kwargs}]
  (validate-keyword-arguments (keys kwargs)
                              count-required-kwargs)
  (let [cnt (-> (Count/builder)
                (.maxPartitionsContributed max-partitions-contributed)
                (.epsilon epsilon)
                (cond->
                 delta (.delta delta)
                 noise (.noise noise))
                .build)]
    (.incrementBy cnt (if (number? data-seq-or-cnt)
                        data-seq-or-cnt
                        (size data-seq-or-cnt)))
    (.computeResult cnt)))


(defn laplace-noise [] (LaplaceNoise.))

(defn gaussian-noise [] (GaussianNoise.))
