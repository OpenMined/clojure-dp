(ns differential-privacy-clj.core
  ^{:doc "A collection of differentially private algorithms.

          This library is a wrapper for Google differential-privacy
          https://github.com/google/differential-privacy"}
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


(defn- validate-keyword-arguments [passed-kwargs required-kwargs]
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


(defn- bounded-algorithm [data-seq
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


(defn bounded-sum
  "
  Calculates a differentially private sum for a sequence of values.

  Example: (bounded-sum [1 2 3 5] :epsilon 0.1
                                  :lower 0 :upper 10
                                  :max-partitions-contributed 1)


  Required keyword arguments:

    :epsilon - Epsilon DP parameter.

    :lower - Lower bound for the entries added to the sum. Any data values
             below this value will be clamped (i.e. set) to this bound.

    :upper - Upper bound for the entries added to the sum. Any data value
             above this value will be clamped (i.e. set) to this bound.

    :max-partitions-contributed - Maximum number of partitions to which
                                  a single privacy unit (e.g. an individual)
                                  is allowed to contribute.


  Optional keyword arguments:

    :delta - Delta DP parameter (ignored when Laplace noise is used).

    :noise - Distribution from which the noise will be generated and added
             to the sum (e.g. `(GaussianNoise.)`).
             The Java library underneath will use `LaplaceNoise` by default.

    :chunk-size - Data is added in chunks of this size when specified.
                  The default value is `default-data-chunk-size`.

  "
  [data-seq & args]
  (apply bounded-algorithm
         data-seq
         (BoundedSum/builder)
         bounded-sum-required-kwargs
         args))


(defn bounded-mean
  "
  Calculates a differentially private average for a sequence of values.


  Example:

    (bounded-mean [1 2 3 4 5] :epsilon 0.1
                              :lower 0 :upper 10
                              :max-partitions-contributed 1
                              :max-contributions-per-partition 1)

  Required keyword arguments:

    :epsilon - Epsilon DP parameter.

    :lower - Lower bound for the entries added to the mean. Any data values
             below this value will be clamped (i.e. set) to this bound.

    :upper - Upper bound for the entries added to the sum. Any data value
             above this value will be clamped (i.e. set) to this bound.

    :max-partitions-contributed - Maximum number of partitions a single privacy unit
                                  (e.g. an individual) is allowed to contribute to.

    :max-contributions-per-partition - Maximum number of contributions per partition
                                       from a single privacy unit (e.g. an individual).


  Optional keyword arguments:

    :delta - Delta DP parameter (ignored when Laplace noise is used).

    :noise - Distribution from which the noise will be generated and added
             to the average (e.g. `(GaussianNoise.)`).
             The Java library underneath will use `LaplaceNoise` by default.

    :chunk-size - Data is processed in chunks of this size when specified.
                  The default value is `default-data-chunk-size`.

  "
  [data-seq & args]
  (apply bounded-algorithm
         data-seq
         (BoundedMean/builder)
         bounded-mean-required-kwargs
         args))


(defn count
  "
  Calculates a differentially private count given a sequence of values
  or the true count.

  This function assumes that each privacy unit may contribute to a single
  partition only once (i.e. only one data contribution per privacy unit
  per partition).

  Examples:

    (count [1 2 3 4 5] :epsilon 0.1
                       :max-partitions-contributed 1)

    (count 5 :epsilon 0.1
             :max-partitions-contributed 1)


  Required keyword arguments:

    :max-partitions-contributed - Maximum number of partitions a single privacy unit
                                  (e.g. an individual) is allowed to contribute to.

    :epsilon - Epsilon DP parameter.


  Optional keyword arguments:

    :delta - Delta DP parameter (ignored when Laplace noise is used).

    :noise - Distribution from which the noise will be generated and added
             to the average (e.g. `(GaussianNoise.)`).
             The Java library underneath will use `LaplaceNoise` by default.

  "
  [data-seq-or-cnt
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


(defn laplace-noise
  "
  Generates Laplace noise.
  "
  [] (LaplaceNoise.))


(defn gaussian-noise
  "
  Generates Gaussian noise.
  "
  [] (GaussianNoise.))
