(ns com.example.hanoi-game
  (:require [clojure.spec.alpha :as s]))

;; Specs

(s/def ::tag #{:tile})
(s/def ::size pos-int?)
(s/def ::tile (s/keys :req [::tag ::size]))

(defn tiles-in-desc-order? [pillar]
  (or (empty? pillar)
      (->> pillar (map ::size) (apply >))))

(s/def ::pillar (s/and (s/coll-of ::tile) tiles-in-desc-order?))
(s/def ::game (s/tuple ::pillar ::pillar ::pillar))
(s/def ::move (s/and
               (s/cat :from nat-int?
                      :to nat-int?)
               (fn [{:keys [from to]}] (not= from to))))

(s/fdef ->tile
        :args (s/cat :size ::size)
        :ret  ::tile)

(s/fdef create
        :args (s/cat :size pos-int?)
        :ret  ::game)

(defn valid-pillar?
  ([k {:keys [move game]}]
   (< (move k) (count game)))
  ([k] (partial valid-pillar? k)))

(s/fdef move
        :args (s/and (s/cat :game ::game
                            :move ::move)
                     (valid-pillar? :from)
                     (valid-pillar? :to))
        :ret ::game
        :fn #(= (-> % :ret count)
                (-> % :args :game count)))

;; Model

(defn ->tile [size] 
  {::tag :tile ::size size})

(defn create [max-disc-size]
  (let [number-of-towers 3]
    (into [(vec (map ->tile (range max-disc-size 0 -1)))] (repeat (dec number-of-towers) []))))

(defn valid-move? [game [from to]]
  (let [{piece-to-move ::size}  (last (game from))
        {piece-to-cover ::size} (last (game to))]
    (and piece-to-move (or (nil? piece-to-cover) (< piece-to-move piece-to-cover)))))

(defn move [game [from to :as move]]
  {:pre [(valid-pillar? from game) (valid-pillar? to game)]
   :post [(s/valid? ::game %)]}
  (when-not (try (valid-move? game move) (catch Throwable e))
    (throw (ex-info "not a valid move" {:game game :move move})))
  (-> game
      (update from (comp vec butlast))
      (update to conj (last (get game from)))))

;; Solver

(defn solve [game]
  (when (-> game count (< 3))
    (throw (ex-info "Cannot solve for less than 3 towers" {:game game})))
  (let [moves (transient [])
        solve (fn solve [n [a b c & cols]]
                (when (pos? n)
                  (solve (dec n) (into [a c b] cols))
                  (conj! moves [a c])
                  (solve (dec n) (into [b a c] cols))))]
    (solve (::size (ffirst game)) (range (count game)))
    (persistent! moves)))

