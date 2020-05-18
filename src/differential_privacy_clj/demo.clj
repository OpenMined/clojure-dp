(ns differential-privacy-clj.demo
  (:require [differential-privacy-clj.core :as dp]))


(def example-sum
  (dp/bounded-sum :lower 0.0 :upper 10.0 :max-partitions 12 :epsilon 1.0))


(def example-count
  (dp/count :max-partitions 12 :epsilon 1.0))


(def example-data [10.0 200.0 -1.0 10000.0 100000.0 10000000.0 1000000000.0])


(def private-sum-result
  (dp/compute! example-sum example-data))


(def private-count-result
  (dp/compute! example-count example-data))


(println)
(println "Private sum:\t" private-sum-result)
(println "Private count:\t" private-count-result)

;; This also works:
#_(println "Gaussian noise:\t" (dp/gaussian-noise))
#_(println "Laplacian noise:\t" (dp/laplace-noise))
