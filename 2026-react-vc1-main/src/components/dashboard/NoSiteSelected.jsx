import { Package } from "lucide-react";

export default function NoSiteSelected() {
  return (
    <div className="flex flex-col items-center gap-3 text-center">
            {/* Dozen icoon */}
            <Package size={64} className="text-gray-300" strokeWidth={1.5} />

            <div>
              <p className="text-lg font-medium text-gray-700">Selecteer een site</p>
              <p className="text-sm text-gray-400 mt-1 max-w-xs">
                Selecteer een site op de kaart of in het menu om een dashboard met meer info te zien te krijgen.
              </p>
            </div>
          </div>
  );
}