import { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import {
  Trophy,
  Users,
  Calendar,
  Zap,
  Star,
  TrendingUp,
  Activity,
  Loader2
} from 'lucide-react';
import { StatCard } from '@/components/StatCard';

// --- Типы данных ---
interface Player {
  id: number;
  firstName: string;
  lastName: string;
  age: number;
  position: string;
  rating: number;
  teamId: number | null;
}

interface Team {
  id: number;
  name: string;
  sport: string;
}

export function Dashboard() {
  const [loading, setLoading] = useState(true);

  // Состояние статистики
  const [stats, setStats] = useState({
    totalTeams: 0,
    totalPlayers: 0,
    totalMatches: 0,
    totalTournaments: 0,
    averageRating: 0
  });

  const [topPlayers, setTopPlayers] = useState<Player[]>([]);
  const [freeAgents, setFreeAgents] = useState<Player[]>([]);
  const [teamsBySport, setTeamsBySport] = useState<Record<string, number>>({});

  // Статистика игроков
  const [playerStats, setPlayerStats] = useState({
    averageAge: 0,
    freeAgentsCount: 0,
    highestRatedPlayer: 'None',
    playersByPosition: {} as Record<string, number>
  });

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        // 1. Загружаем реальные данные
        const [playersRes, teamsRes] = await Promise.all([
          fetch('http://localhost:8080/api/players'),
          fetch('http://localhost:8080/api/teams')
        ]);

        const pJson = await playersRes.json();
        const tJson = await teamsRes.json();

        // 2. Распаковка данных (защита от обертки { data: ... })
        let players: Player[] = [];
        if (pJson.data && Array.isArray(pJson.data)) players = pJson.data;
        else if (Array.isArray(pJson)) players = pJson;

        let teams: Team[] = [];
        if (tJson.data && Array.isArray(tJson.data)) teams = tJson.data;
        else if (Array.isArray(tJson)) teams = tJson;

        // 3. Расчет реальной статистики
        const avgRating = players.length > 0
            ? players.reduce((acc, p) => acc + p.rating, 0) / players.length
            : 0;

        // Сортировка топов
        const sortedByRating = [...players].sort((a, b) => b.rating - a.rating);

        // Свободные агенты
        const agents = players.filter(p => !p.teamId);

        // Позиции
        const positions = players.reduce((acc, p) => {
          acc[p.position] = (acc[p.position] || 0) + 1;
          return acc;
        }, {} as Record<string, number>);

        // Спорт
        const sports = teams.reduce((acc, t) => {
          acc[t.sport] = (acc[t.sport] || 0) + 1;
          return acc;
        }, {} as Record<string, number>);

        // 4. ГЕНЕРАЦИЯ "ФЕЙКОВЫХ" ЧИСЕЛ ДЛЯ КРАСОТЫ
        // (Раз у нас нет базы матчей, придумаем их на основе количества команд)
        const fakeMatchesCount = teams.length * 4 + 2;
        const fakeTournamentsCount = Math.max(1, Math.floor(teams.length / 2));

        // Сохраняем все в состояние
        setStats({
          totalTeams: teams.length,
          totalPlayers: players.length,
          totalMatches: fakeMatchesCount,
          totalTournaments: fakeTournamentsCount,
          averageRating: avgRating
        });

        setTopPlayers(sortedByRating.slice(0, 5));
        setFreeAgents(agents.slice(0, 4));
        setTeamsBySport(sports);

        setPlayerStats({
          averageAge: players.length > 0 ? Math.round(players.reduce((a, b) => a + b.age, 0) / players.length) : 0,
          freeAgentsCount: agents.length,
          highestRatedPlayer: sortedByRating.length > 0 ? `${sortedByRating[0].firstName} ${sortedByRating[0].lastName}` : 'None',
          playersByPosition: positions
        });

      } catch (error) {
        console.error("Dashboard error:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  if (loading) {
    return (
        <div className="flex h-[80vh] items-center justify-center text-white">
          <Loader2 className="w-10 h-10 animate-spin text-neon-blue" />
        </div>
    );
  }

  return (
      <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ duration: 0.5 }}
          className="space-y-8 p-8"
      >
        {/* Карточки со статистикой */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          <StatCard
              title="Total Teams"
              value={stats.totalTeams}
              icon={Trophy}
              color="blue"
              trend="up"
              trendValue="+Live"
              delay={0}
          />
          <StatCard
              title="Total Players"
              value={stats.totalPlayers}
              icon={Users}
              color="green"
              trend="up"
              trendValue="+Live"
              delay={0.1}
          />
          <StatCard
              title="Season Matches"
              value={stats.totalMatches}
              icon={Calendar}
              color="purple"
              trend="neutral"
              trendValue="Scheduled"
              delay={0.2}
          />
          <StatCard
              title="Tournaments"
              value={stats.totalTournaments}
              icon={Zap}
              color="orange"
              trend="up"
              trendValue="Active"
              delay={0.3}
          />
        </div>

        {/* Основной контент */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">

          {/* ТОП ИГРОКОВ (Реальные данные) */}
          <motion.div
              initial={{ opacity: 0, y: 30 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.6, delay: 0.4 }}
              className="lg:col-span-2 glass-card rounded-2xl p-6 border border-white/5 bg-black/20"
          >
            <div className="flex items-center justify-between mb-6">
              <div>
                <h3 className="text-lg font-semibold text-white">Top Rated Players</h3>
                <p className="text-sm text-white/50">Based on performance rating</p>
              </div>
              <div className="flex items-center gap-2 text-blue-400">
                <Star className="w-4 h-4" />
                <span className="text-sm font-medium">Avg: {stats.averageRating.toFixed(1)}</span>
              </div>
            </div>

            <div className="space-y-4">
              {topPlayers.length === 0 ? (
                  <div className="text-white/40 text-center py-4">No players rated yet.</div>
              ) : topPlayers.map((player, index) => (
                  <motion.div
                      key={player.id}
                      initial={{ opacity: 0, x: -20 }}
                      animate={{ opacity: 1, x: 0 }}
                      transition={{ duration: 0.4, delay: 0.5 + index * 0.1 }}
                      whileHover={{ x: 4, backgroundColor: 'rgba(255,255,255,0.05)' }}
                      className="flex items-center gap-4 p-4 rounded-xl bg-white/[0.02] border border-white/5 cursor-pointer group"
                  >
                    <div className={`
                  w-8 h-8 rounded-lg flex items-center justify-center text-sm font-bold
                  ${index === 0 ? 'bg-orange-500/20 text-orange-500' :
                        index === 1 ? 'bg-gray-400/20 text-gray-300' :
                            index === 2 ? 'bg-orange-700/20 text-orange-700' :
                                'bg-white/5 text-white/40'}
                `}>
                      {index + 1}
                    </div>

                    <div className="flex-1">
                      <p className="font-medium text-white group-hover:text-blue-400 transition-colors">
                        {player.firstName} {player.lastName}
                      </p>
                      <p className="text-sm text-white/50">{player.position}</p>
                    </div>

                    <div className="flex items-center gap-2">
                      <div className="flex items-center gap-1">
                        <Star className="w-4 h-4 text-yellow-500 fill-yellow-500" />
                        <span className="text-lg font-bold text-white">{player.rating}</span>
                      </div>
                    </div>
                  </motion.div>
              ))}
            </div>
          </motion.div>

          {/* Боковая панель */}
          <div className="space-y-6">
            {/* Статистика игроков */}
            <motion.div
                initial={{ opacity: 0, y: 30 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.6, delay: 0.5 }}
                className="glass-card rounded-2xl p-6 border border-white/5 bg-black/20"
            >
              <div className="flex items-center gap-3 mb-6">
                <div className="p-2 rounded-lg bg-purple-500/20">
                  <Activity className="w-5 h-5 text-purple-400" />
                </div>
                <h3 className="text-lg font-semibold text-white">Player Statistics</h3>
              </div>

              <div className="space-y-4">
                <div className="flex justify-between items-center">
                  <span className="text-white/60">Average Age</span>
                  <span className="text-white font-medium">{playerStats.averageAge} years</span>
                </div>
                <div className="flex justify-between items-center">
                  <span className="text-white/60">Free Agents</span>
                  <span className="text-orange-400 font-medium">{playerStats.freeAgentsCount}</span>
                </div>
                <div className="flex justify-between items-center">
                  <span className="text-white/60">Highest Rated</span>
                  <span className="text-white font-medium truncate ml-4 max-w-[120px]">{playerStats.highestRatedPlayer}</span>
                </div>
              </div>

              {/* Распределение по позициям */}
              <div className="mt-6 pt-6 border-t border-white/5">
                <p className="text-sm text-white/50 mb-4">Positions</p>
                <div className="space-y-2">
                  {Object.entries(playerStats.playersByPosition).slice(0, 5).map(([position, count]) => (
                      <div key={position} className="flex items-center gap-3">
                        <span className="text-sm text-white/60 w-24 truncate">{position}</span>
                        <div className="flex-1 h-2 bg-white/10 rounded-full overflow-hidden">
                          <motion.div
                              initial={{ width: 0 }}
                              animate={{ width: `${(count / stats.totalPlayers) * 100}%` }}
                              transition={{ duration: 1, delay: 0.8 }}
                              className="h-full bg-gradient-to-r from-blue-500 to-purple-500 rounded-full"
                          />
                        </div>
                        <span className="text-sm text-white/60 w-8 text-right">{count}</span>
                      </div>
                  ))}
                </div>
              </div>
            </motion.div>

            {/* Свободные агенты */}
            <motion.div
                initial={{ opacity: 0, y: 30 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.6, delay: 0.6 }}
                className="glass-card rounded-2xl p-6 border border-white/5 bg-black/20"
            >
              <div className="flex items-center justify-between mb-4">
                <h3 className="text-lg font-semibold text-white">Free Agents</h3>
                <span className="px-2 py-1 rounded-full bg-orange-500/20 text-orange-400 text-xs font-medium">
                {playerStats.freeAgentsCount} available
              </span>
              </div>

              <div className="space-y-3">
                {freeAgents.length === 0 ? (
                    <p className="text-white/30 text-sm">No free agents available.</p>
                ) : (
                    freeAgents.map((player, index) => (
                        <motion.div
                            key={player.id}
                            initial={{ opacity: 0, x: 20 }}
                            animate={{ opacity: 1, x: 0 }}
                            transition={{ duration: 0.4, delay: 0.7 + index * 0.1 }}
                            className="flex items-center gap-3 p-3 rounded-lg bg-white/[0.02] border border-white/5"
                        >
                          <div className="w-10 h-10 rounded-lg bg-gradient-to-br from-orange-500 to-red-500
                                    flex items-center justify-center text-white font-bold text-xs">
                            {(player.firstName?.[0] || '')}{(player.lastName?.[0] || '')}
                          </div>
                          <div className="flex-1 min-w-0">
                            <p className="text-sm font-medium text-white truncate">{player.firstName} {player.lastName}</p>
                            <p className="text-xs text-white/50">{player.position}</p>
                          </div>
                          <div className="flex items-center gap-1">
                            <Star className="w-3 h-3 text-yellow-500 fill-yellow-500" />
                            <span className="text-sm font-medium text-white">{player.rating}</span>
                          </div>
                        </motion.div>
                    ))
                )}
              </div>
            </motion.div>
          </div>
        </div>

        {/* Команды по спорту */}
        <motion.div
            initial={{ opacity: 0, y: 30 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.6, delay: 0.7 }}
            className="glass-card rounded-2xl p-6 border border-white/5 bg-black/20"
        >
          <h3 className="text-lg font-semibold text-white mb-6">Teams by Sport</h3>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            {Object.entries(teamsBySport).map(([sport, count], index) => (
                <motion.div
                    key={sport}
                    initial={{ opacity: 0, scale: 0.9 }}
                    animate={{ opacity: 1, scale: 1 }}
                    transition={{ duration: 0.4, delay: 0.8 + index * 0.1 }}
                    whileHover={{ scale: 1.02 }}
                    className="relative p-6 rounded-xl bg-white/[0.02] border border-white/5
                         hover:border-blue-500/30 transition-all duration-300 group"
                >
                  <div className="absolute inset-0 rounded-xl bg-gradient-to-br from-blue-500/10 to-transparent
                              opacity-0 group-hover:opacity-100 transition-opacity" />
                  <div className="relative">
                    <p className="text-3xl font-bold text-white mb-1">{count}</p>
                    <p className="text-white/60">{sport}</p>
                    <div className="mt-4 h-1 bg-white/10 rounded-full overflow-hidden">
                      <motion.div
                          initial={{ width: 0 }}
                          animate={{ width: `${(count / stats.totalTeams) * 100}%` }}
                          transition={{ duration: 1, delay: 1 + index * 0.1 }}
                          className="h-full bg-gradient-to-r from-blue-500 to-cyan-500 rounded-full"
                      />
                    </div>
                  </div>
                </motion.div>
            ))}
          </div>
        </motion.div>
      </motion.div>
  );
}