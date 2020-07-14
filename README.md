# differential-privacy-clj

A Clojure wrapper for the Java [differential-privacy library](https://github.com/google/differential-privacy).

See [online demo notebooks](https://mybinder.org/v2/gh/OpenMined/JavaDP/master/?filepath=%2Fdoc%2Fclojure).

## Installation

Steps to build it locally:

1. Make sure you have [Maven](https://maven.apache.org/), [Bazel](https://docs.bazel.build/versions/master/install.html) and [Leiningen](https://leiningen.org/) installed.

2. Download and build the Java [differential-privacy library](https://github.com/google/differential-privacy):
```
git clone https://github.com/google/differential-privacy.git
cd differential-privacy/java/
bazel build ...
```

3. Install `libdifferentialprivacy.jar` in your local Maven repository:

```
mvn install:install-file -Dfile=bazel-bin/main/com/google/privacy/differentialprivacy/libdifferentialprivacy.jar -DgroupId=com.google.privacy.differentialprivacy -DartifactId=libdifferentialprivacy -Dversion=1.0 -Dpackaging=jar
```

4. Clone this repository and install the library:

```
git clone https://github.com/OpenMined/clojure-dp.git
cd clojure-dp
lein install
```

## Usage

See [demo notebooks](https://github.com/OpenMined/JavaDP/tree/master/doc/clojure) ([run online](https://mybinder.org/v2/gh/OpenMined/JavaDP/master/?filepath=%2Fdoc%2Fclojure)).

Examples:

```clojure
(require '[differential-privacy-clj.core :as dp])

(def example-data (take 1000 (repeatedly #(rand 10.0))))

(println "True sum: "
         (reduce + example-data))
(println "DP sum:   "
         (dp/bounded-sum example-data :epsilon 0.1
                         :lower 0 :upper 10
                         :max-partitions-contributed 1))

(println "True count:"
         (count example-data))
(println "DP count:  "
         (dp/count example-data :epsilon 0.1
                   :max-partitions-contributed 1))

(println "True mean:" (/ (reduce + example-data)
                         (count example-data)))
(println "DP mean:" (dp/bounded-mean example-data :epsilon 0.1
                                     :lower 0 :upper 10
                                     :max-partitions-contributed 1
                                     :max-contributions-per-partition 1))
```
will print something like:
```
True sum:  4988.542973798648
DP sum:    5175.075793958153
True count: 1000
DP count:   999
True mean: 4.988542973798648
DP mean: 5.002603661455349
```

## Available algorithms:

:heavy_check_mark: - Supported :white_check_mark: - Planned

| Algorithm          |                    |
|--------------------|--------------------|
| Count              | :heavy_check_mark: |
| Sum                | :heavy_check_mark: |
| Mean               | :heavy_check_mark: |
| Variance           | :white_check_mark: |
| Standard deviation | :white_check_mark: |
| Order statistics (incl. min, max, and median) | :white_check_mark: |


## Support

For support in using this library, please join the **#lib_dp_java** Slack channel. If you’d like to follow along with any code changes to the library, please join the **#code_dp_java** Slack channel. [Click here to join our Slack community!](https://slack.openmined.org)
