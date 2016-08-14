(ns dark-forces.systems
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [basic-particles.records.vector2d :refer :all]
            [basic-particles.protocols.vector :as v]
            [basic-ces.core :refer :all]))

(defn wind [entities]
  (let [entities-with-mass (filter-entities entities :mass :velocity)]
    (reduce (fn [agg [id components]]
              (let [mass (:mass components)
                    wind (->Vector2D -0.01 0)
                    acceleration (v/divide wind mass)]
                (update-in agg [id :velocity] v/add acceleration)))
            entities
            entities-with-mass)))

(defn gravity [entities]
  (let [entities-with-mass (filter-entities entities :mass :velocity)]
    (reduce (fn [agg [id components]]
              (let [mass (:mass components)
                    gravity (->Vector2D 0 0.05)
                    acceleration (v/divide gravity mass)]
                (update-in agg [id :velocity] v/add acceleration)))
            entities
            entities-with-mass)))

(defn calculate-gravitational-force [pos1 mass1 pos2 mass2 max-range]
  (let [g 0.8
        dir (v/subtract pos1 pos2)
        dir-u (v/normalize dir)
        dist (v/magnitude dir)
        dist-squared (q/sq dist)]
    (if (> dist max-range)
      (->Vector2D 0 0)
      (v/multiply dir-u (-> g (* mass1) (* mass2) (/ dist-squared))))))

(defn attract-and-repel-with-mouse [entities]
  (let [entities-with-mass (filter-entities entities :mass :velocity)
        mouse-position (->Vector2D (q/mouse-x) (q/mouse-y))
        mouse-mass 1000
        max-range 100]
    (reduce (fn [agg [id components]]
              (let [mass (:mass components)
                    pos (:position components)
                    gravity (calculate-gravitational-force pos mass mouse-position mouse-mass max-range)
                    accel (v/divide gravity (if (q/mouse-pressed?) (* mass -1) mass))]
                (update-in agg [id :velocity] v/add accel)))
            entities
            entities-with-mass)))

(defn mover [entities]
  (let [movers (filter-entities entities :position :velocity)]
    (reduce (fn [agg [id components]]
              (let [velocity (:velocity components)]
                (update-in agg [id :position] v/add velocity)))
            entities
            movers)))

(defn angular-rotation [entities]
  (let [movers (filter-entities entities :angle :velocity)]
    (reduce (fn [agg [id components]]
              (let [velocity (:velocity components)
                    angular-velocity (/ (:x velocity) 10)]
                (update-in agg [id :angle] + angular-velocity)))
            entities
            movers)))

(defn degeneration [entities]
  (let [degenerative-entities (filter-entities entities :lifespan)]
    (reduce (fn [agg [id components]]
              (let [lifespan (:lifespan components)
                    new-lifespan (- lifespan 2)]
                 (if (< new-lifespan 0)
                   (dissoc agg id)
                   (assoc-in agg [id :lifespan] new-lifespan))))
            entities
            degenerative-entities)))

(defn particle-emitter [entities create-particle-func]
  (let [emitters (filter-entities entities :particle-emitter :position)]
    (reduce (fn [agg [id components]]
              (let [position (:position components)]
                 (merge agg (create-particle-func position))))
            entities
            emitters)))

(defn renderer [entities]
  (doseq [[id components] (filter-entities entities :renderable)]
    (let [render-func (:renderable components)]
      (render-func components)))
  entities)