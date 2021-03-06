(ns clouds.cloud-system
  (:require [quil.core :as q]
            [basic-particles.records.vector2d :refer :all]
            [basic-particles.protocols.particle :refer :all]
            [basic-particles.protocols.vector :refer :all]
            [basic-particles.protocols.particle-list :refer :all]
            [clouds.puff :refer :all]))

(defn -puff []
  (let [location (->Vector2D 0 0)
        velocity (->Vector2D (q/random -0.6 0.6) (q/random -0.6 0.6))
        lifespan 255
        variance (q/random 0 25)]
    (->Puff location velocity lifespan variance)))

(defrecord CloudSystem [location particles]
  
  Particle
  
  (step [ps [& forces]]
  	(let [updated-particles (filter is-alive? (map (fn [p] (step p forces)) particles))]
     (append (CloudSystem. location updated-particles) [(-puff)])))
  
  (render [ps]
    (q/with-translation [(:x location) (:y location)]
  	   (doseq [p particles]
          (render p))))
  
  (is-alive? [ps]
    (not (empty? (filter (fn [p] (is-alive? p)) particles)))))

(extend-type CloudSystem
  
  ParticleList
  
  (append [ps [& new-particles]]
    (CloudSystem. (:location ps) (apply conj (:particles ps) new-particles))))