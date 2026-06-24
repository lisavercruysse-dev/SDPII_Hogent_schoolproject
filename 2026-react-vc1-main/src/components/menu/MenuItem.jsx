export default function MenuItem({ name, icon: Icon, badgeCount }) {
  return (
    <div className="flex gap-3 items-center px-8 py-3 hover:cursor-pointer hover:text-[#CD1212] relative">
      <div className="relative">
        <Icon size="25" />
        {badgeCount > 0 && (
          <span className="absolute -top-1.5 -right-1.5 flex items-center justify-center min-w-[16px] h-4 px-0.5 rounded-full bg-[#CD1212] text-white text-[10px] font-bold leading-none">
            {badgeCount > 99 ? "99+" : badgeCount}
          </span>
        )}
      </div>
      <p>{name}</p>
    </div>
  );
}
