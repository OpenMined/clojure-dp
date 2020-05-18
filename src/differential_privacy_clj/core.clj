(ns differential-privacy-clj.core
  (:import [com.google.privacy.differentialprivacy
            BoundedSum Count LaplaceNoise GaussianNoise]
           [com.google.protobuf
            InvalidProtocolBufferException])
  (:require [clojure.reflect :as rfl]
            [clojure.pprint :as pp])
  (:refer-clojure :rename {count size}))  ;; "count" is used for private count in this ns


;; Useful for development:
(defn show-public-members [obj]
  (pp/pprint (filter (comp :public :flags) (:members (rfl/reflect obj)))))


(defn bounded-sum [& {:keys [lower upper max-partitions epsilon]}]
  (-> (BoundedSum/builder)
      (.lower lower)
      (.upper upper)
      (.maxPartitionsContributed max-partitions)
      (.epsilon epsilon)
      .build))


(defn count [& {:keys [max-partitions epsilon]}]
  (-> (Count/builder)
      (.maxPartitionsContributed max-partitions)
      (.epsilon epsilon)
      .build))


(defn laplace-noise [] (LaplaceNoise.))

(defn gaussian-noise [] (GaussianNoise.))


(defprotocol Algorithm
  (compute!
    [algorithm]
    [algorithm data]))


(extend-protocol Algorithm

  BoundedSum
  (compute!
    ([algorithm]
     (.computeResult algorithm))
    ([algorithm data]
     (.addEntries algorithm data)
     (compute! algorithm)))

  Count
  (compute!
    ([algorithm]
     (.computeResult algorithm))
    ([algorithm data]
     (.incrementBy algorithm (size data))
     (compute! algorithm))))
