import { useState, useRef, useEffect } from "react";
import { Search, MapPin, Users, ArrowRight  } from "lucide-react";
import useSWR from "swr";
import { getAll } from "../../api";

const statusStyles = {
  ACTIEF: "bg-green-100 text-green-700",
  INACTIEF: "bg-red-100 text-red-600",
};

const healthStyles = {
  GEZOND: "bg-green-100 text-green-700",
  OFFLINE: "bg-red-100 text-red-600",
  PROBLEMEN: "bg-yellow-100 text-yellow-700",
};

export default function MapAndSitesPanel({ 
  onSiteSelect, 
  selectedSiteId,
  onDetails}) {
  const mapRef = useRef(null);
  const markersRef = useRef([]);
  const leafletMapRef = useRef(null);
  const [search, setSearch] = useState("");

  const { data: places = [], error, isLoading } = useSWR("sites", getAll);
  console.log("places:", places);
  console.log("isLoading:", isLoading);
  console.log("error:", error);

  const filteredSites = places.filter(
    (s) =>
      s.name.toLowerCase().includes(search.toLowerCase()) ||
      s.locatie.toLowerCase().includes(search.toLowerCase()),
  );

  // Initialiseer Leaflet map (eenmalig)
  useEffect(() => {
    let isUnmounted = false;

    if (leafletMapRef.current || mapRef.current?._leaflet_id) return;

    const initMap = async () => {
      // CSS laden als die er nog niet is
      if (!document.getElementById("leaflet-css")) {
        const link = document.createElement("link");
        link.id = "leaflet-css";
        link.rel = "stylesheet";
        link.href = "https://unpkg.com/leaflet@1.9.4/dist/leaflet.css";
        document.head.appendChild(link);
      }

      // Leaflet JS dynamisch laden
      if (!window.L) {
        await new Promise((resolve) => {
          const script = document.createElement("script");
          script.src = "https://unpkg.com/leaflet@1.9.4/dist/leaflet.js";
          script.onload = resolve;
          document.head.appendChild(script);
        });
      }

      const L = window.L;

      if (isUnmounted || !mapRef.current || mapRef.current._leaflet_id) {
        return;
      }

      const map = L.map(mapRef.current, {
        center: [30, 15],
        zoom: 2,
        zoomControl: true,
        scrollWheelZoom: false,
        maxBounds: [
          [-90, -180],
          [90, 180],
        ],
      });

      L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
        attribution: "© OpenStreetMap",
        noWrap: true,
      }).addTo(map);

      leafletMapRef.current = map;
    };

    initMap();

    return () => {
      isUnmounted = true;
      if (leafletMapRef.current) {
        leafletMapRef.current.remove();
        leafletMapRef.current = null;
      }
    };
  }, [onSiteSelect]);

  // Markers toevoegen/bijwerken wanneer places geladen zijn
  useEffect(() => {
    if (!leafletMapRef.current || !window.L || places.length === 0) return;

    const L = window.L;

    // Bestaande markers verwijderen
    markersRef.current.forEach((m) => m.remove());
    markersRef.current = [];

    // Markers toevoegen
    places.forEach((site) => {
      const icon = L.divIcon({
        className: "",
        html: `<div style="
          width: 14px; height: 14px;
          background: ${site.operationeleStatus === "ACTIEF" ? "#8fce00" : "#f44336"};
          border: 2px solid white;
          border-radius: 50%;
          box-shadow: 0 1px 4px rgba(0,0,0,0.35);
          cursor: pointer;
        "></div>`,
        iconSize: [14, 14],
        iconAnchor: [6, 6],
      });

      const marker = L.marker([site.breedtegraad, site.lengtegraad], { icon })
        .addTo(leafletMapRef.current)
        .bindTooltip(site.name, { direction: "top", offset: [0, -8] })
        .on("click", () => onSiteSelect(site));

        markersRef.current.push(marker);
    });
  }, [places, onSiteSelect]);  

  // Zoom/fly naar geselecteerde site
  useEffect(() => {
    if (leafletMapRef.current && selectedSiteId) {
      const site = places.find((s) => s.id === selectedSiteId);
      if (site) {
        leafletMapRef.current.flyTo([site.breedtegraad, site.lengtegraad], 6, {
          duration: 1.2,
        });
      }
    }
  }, [selectedSiteId]);

  return (
    <div className="grid grid-cols-[1fr_380px] gap-5 mb-5">
      {/* Map */}
      <div className="rounded-xl overflow-hidden border border-gray-200 shadow-sm h-120">
        <div ref={mapRef} className="w-full h-full" />
      </div>

      {/* Sites panel */}
      <div className="bg-white rounded-xl border border-gray-200 shadow-sm flex flex-col overflow-hidden h-120">
        <div className="p-4 pb-3 shrink-0">
          <h2 className="font-bold text-gray-900 text-base mb-3">Sites</h2>
          <div className="flex items-center gap-2 border border-gray-200 rounded-lg px-3 py-2 bg-gray-50">
            <Search size={14} className="text-gray-400 shrink-0" />
            <input
              type="text"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              placeholder="Zoek sites via naam of locatie..."
              className="bg-transparent text-sm text-gray-700 placeholder-gray-400 outline-none w-full"
            />
          </div>
        </div>

        <div className="overflow-y-auto flex-1 px-3 pb-3 space-y-2">
          {isLoading && (
            <p className="text-sm text-gray-400 text-center mt-6">
              Sites laden…
            </p>
          )}

          {error && (
            <p className="text-sm text-red-400 text-center mt-6">
              Fout bij het laden van sites.
            </p>
          )}


          {!isLoading && !error && filteredSites.length === 0 && (
            <p className="text-sm text-gray-400 text-center mt-6">
              Geen sites gevonden
            </p>
          )}

          {filteredSites.map((site) => {
            const isSelected = selectedSiteId === site.id;
            return (
              <div
                key={site.id}
                onClick={() => onSiteSelect(site)}
                className={`rounded-xl border p-2 cursor-pointer transition-all ${
                  isSelected
                    ? "border-gray-900 bg-gray-50"
                    : "border-gray-200 bg-white hover:border-gray-300"
                }`}
              >
                <div className="flex items-start justify-between gap-2 mb-2">
                  <div className="flex items-center gap-1.5 min-w-0">
                    <MapPin
                      size={13}
                      className="text-gray-400 shrink-0 mt-0.5"
                    />
                    <div className="min-w-0">
                      <p className="font-semibold text-sm text-gray-900 truncate">
                        {site.name}
                      </p>
                      <p className="text-xs text-gray-400 truncate">
                        {site.locatie}, {site.land}
                      </p>
                    </div>
                  </div>

                  <div className="flex items-center gap-1 shrink-0">
                    <span
                      className={`text-xs font-medium px-2 py-0.5 rounded-full ${statusStyles[site.operationeleStatus]}`}
                    >
                      {site.operationeleStatus}
                    </span>
                    <span
                      className={`text-xs font-medium px-2 py-0.5 rounded-full ${healthStyles[site.siteProductieStatus]}`}
                    >
                      {site.siteProductieStatus}
                    </span>
                  </div>
                </div>

                <div className="flex items-center justify-between gap-2">
                <div className="flex items-center gap-1.5">
                  <Users size={13} className="text-gray-400" />
                  <span className="text-xs text-gray-600">
                    <span className="font-semibold">
                      {site.availableWorkers} / {site.werknemerCount}
                    </span>{" "}
                    werknemers
                  </span>
                </div>

                  <button
                    onClick={onDetails}
                    className="px-4 py-1 bg-gray-200 hover:bg-gray-300 text-gray-800 text-sm rounded-md transition-colors"
                  >
                    Machinepark
                    <ArrowRight size={13} className="inline-block ml-2" strokeWidth={1.5} />
                  </button>
                </div>
                
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
}
