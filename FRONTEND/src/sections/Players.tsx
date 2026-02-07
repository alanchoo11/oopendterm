import { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import {
  Star,
  Calendar,
  Plus,
  Search,
  Filter,
  ArrowUpDown,
  Loader2,
  MoreHorizontal,
  Pencil, // Иконка редактирования
  Trash2, // Иконка удаления
  X
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
  DialogFooter,
} from "@/components/ui/dialog";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";

// --- Types ---
interface Player {
  id: number;
  firstName: string;
  lastName: string;
  age: number;
  position: string;
  rating: number;
  teamId: number | null;
  jerseyNumber: number;
}

interface Team {
  id: number;
  name: string;
}

export function Players() {
  const [players, setPlayers] = useState<Player[]>([]);
  const [teams, setTeams] = useState<Team[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  // UI State
  const [searchQuery, setSearchQuery] = useState('');
  const [sortField, setSortField] = useState<keyof Player>('rating');
  const [sortDirection, setSortDirection] = useState<'asc' | 'desc'>('desc');

  // Modal State
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null); // Если не null, значит редактируем

  // Form State
  const [formData, setFormData] = useState({
    firstName: '', lastName: '', age: '', position: '', rating: '', teamId: '', jerseyNumber: ''
  });

  // --- 1. READ (Load Data) ---
  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [playersRes, teamsRes] = await Promise.all([
        fetch('http://localhost:8080/api/players'),
        fetch('http://localhost:8080/api/teams')
      ]);

      if (!playersRes.ok || !teamsRes.ok) throw new Error('Failed to fetch data');

      const pJson = await playersRes.json();
      const tJson = await teamsRes.json();

      // Unpack Players
      let safePlayers = [];
      if (pJson.data && Array.isArray(pJson.data)) safePlayers = pJson.data;
      else if (Array.isArray(pJson)) safePlayers = pJson;
      setPlayers(safePlayers);

      // Unpack Teams
      let safeTeams = [];
      if (tJson.data && Array.isArray(tJson.data)) safeTeams = tJson.data;
      else if (Array.isArray(tJson)) safeTeams = tJson;
      setTeams(safeTeams);

    } catch (err: any) {
      console.error(err);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  // --- 2. CREATE & UPDATE (Save) ---
  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);

    try {
      const payload = {
        firstName: formData.firstName,
        lastName: formData.lastName,
        age: parseInt(formData.age) || 18,
        position: formData.position,
        rating: parseFloat(formData.rating) || 5.0,
        teamId: (formData.teamId && formData.teamId !== "free") ? parseInt(formData.teamId) : null,
        jerseyNumber: parseInt(formData.jerseyNumber) || 0
      };

      let url = 'http://localhost:8080/api/players';
      let method = 'POST';

      // Если мы в режиме редактирования (есть ID), меняем URL и метод
      if (editingId) {
        url = `http://localhost:8080/api/players/${editingId}`;
        method = 'PUT';
      }

      const response = await fetch(url, {
        method: method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });

      if (response.ok) {
        const jsonResponse = await response.json();
        const savedPlayer = jsonResponse.data || jsonResponse;

        if (savedPlayer && savedPlayer.id) {
          if (editingId) {
            // UPDATE: Заменяем игрока в списке
            setPlayers(prev => prev.map(p => p.id === editingId ? savedPlayer : p));
          } else {
            // CREATE: Добавляем в конец
            setPlayers(prev => [...prev, savedPlayer]);
          }
          closeModal();
        }
      } else {
        alert("Error saving player");
      }
    } catch (error) {
      console.error(error);
      alert("Network error");
    } finally {
      setIsSubmitting(false);
    }
  };

  // --- 3. DELETE ---
  const handleDelete = async (id: number) => {
    if (!window.confirm("Are you sure you want to delete this player?")) return;

    try {
      const response = await fetch(`http://localhost:8080/api/players/${id}`, {
        method: 'DELETE'
      });

      if (response.ok) {
        // Удаляем из состояния UI без перезагрузки
        setPlayers(prev => prev.filter(p => p.id !== id));
      } else {
        alert("Failed to delete player");
      }
    } catch (error) {
      console.error("Delete error:", error);
    }
  };

  // --- Helpers for Modal ---
  const openAddModal = () => {
    setEditingId(null); // Режим добавления
    setFormData({ firstName: '', lastName: '', age: '', position: '', rating: '', teamId: '', jerseyNumber: '' });
    setIsModalOpen(true);
  };

  const openEditModal = (player: Player) => {
    setEditingId(player.id); // Режим редактирования
    setFormData({
      firstName: player.firstName,
      lastName: player.lastName,
      age: player.age.toString(),
      position: player.position,
      rating: player.rating.toString(),
      teamId: player.teamId ? player.teamId.toString() : "free",
      jerseyNumber: player.jerseyNumber.toString()
    });
    setIsModalOpen(true);
  };

  const closeModal = () => {
    setIsModalOpen(false);
    setEditingId(null);
  };

  // --- Sorting & Filtering ---
  const safePlayerList = Array.isArray(players) ? players : [];
  const filteredPlayers = safePlayerList.filter(player => {
    if (!player) return false;
    const name = `${player.firstName || ''} ${player.lastName || ''}`.toLowerCase();
    return name.includes(searchQuery.toLowerCase()) ||
        (player.position || '').toLowerCase().includes(searchQuery.toLowerCase());
  });

  const sortedPlayers = [...filteredPlayers].sort((a, b) => {
    const aValue = a[sortField];
    const bValue = b[sortField];
    if (aValue === bValue) return 0;
    if (aValue === null || aValue === undefined) return 1;
    if (bValue === null || bValue === undefined) return -1;
    if (aValue < bValue) return sortDirection === 'asc' ? -1 : 1;
    if (aValue > bValue) return sortDirection === 'asc' ? 1 : -1;
    return 0;
  });

  const handleSort = (field: keyof Player) => {
    if (sortField === field) {
      setSortDirection(sortDirection === 'asc' ? 'desc' : 'asc');
    } else {
      setSortField(field);
      setSortDirection('asc');
    }
  };

  // --- Style Helpers ---
  const getRatingColor = (rating: number) => {
    if (rating >= 9.0) return 'text-green-400';
    if (rating >= 8.0) return 'text-blue-400';
    if (rating >= 7.0) return 'text-purple-400';
    return 'text-white/60';
  };

  const getRatingBg = (rating: number) => {
    if (rating >= 9.0) return 'bg-green-500/10 border-green-500/20';
    if (rating >= 8.0) return 'bg-blue-500/10 border-blue-500/20';
    if (rating >= 7.0) return 'bg-purple-500/10 border-purple-500/20';
    return 'bg-white/5 border-white/10';
  };

  const getTeamName = (teamId: number | null) => {
    if (!teamId) return null;
    const team = teams.find(t => t.id === teamId);
    return team ? team.name : "Unknown";
  };

  if (error) return <div className="p-10 text-red-500">Error: {error}</div>;

  return (
      <div className="space-y-6 p-6">
        {/* HEADER */}
        <div className="flex flex-col sm:flex-row gap-4 justify-between items-start sm:items-center">
          <div className="relative flex-1 max-w-md">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
            <Input
                placeholder="Search players..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="pl-9 bg-background/50 border-white/10 text-white placeholder:text-white/40"
            />
          </div>

          <div className="flex gap-2 w-full sm:w-auto">
            <Button variant="outline" className="gap-2 border-white/10 hover:bg-white/5 text-white">
              <Filter className="w-4 h-4" />
              Filter
            </Button>

            <Dialog open={isModalOpen} onOpenChange={setIsModalOpen}>
              <DialogTrigger asChild>
                <Button onClick={openAddModal} className="gap-2 bg-blue-600 hover:bg-blue-700 text-white">
                  <Plus className="w-4 h-4" />
                  Add Player
                </Button>
              </DialogTrigger>
              <DialogContent className="sm:max-w-[500px] bg-[#1a1b1e] border-white/10 text-white max-h-[90vh] overflow-y-auto">
                <DialogHeader>
                  <DialogTitle>{editingId ? "Edit Player" : "Add New Player"}</DialogTitle>
                </DialogHeader>
                <form onSubmit={handleSave} className="grid gap-4 py-4">
                  {/* FORM INPUTS */}
                  <div className="grid grid-cols-2 gap-4">
                    <div className="grid gap-2">
                      <Label>First Name</Label>
                      <Input
                          value={formData.firstName}
                          onChange={(e) => setFormData({...formData, firstName: e.target.value})}
                          className="bg-black/20 border-white/10" required
                      />
                    </div>
                    <div className="grid gap-2">
                      <Label>Last Name</Label>
                      <Input
                          value={formData.lastName}
                          onChange={(e) => setFormData({...formData, lastName: e.target.value})}
                          className="bg-black/20 border-white/10" required
                      />
                    </div>
                  </div>

                  <div className="grid grid-cols-2 gap-4">
                    <div className="grid gap-2">
                      <Label>Age</Label>
                      <Input
                          type="number"
                          value={formData.age}
                          onChange={(e) => setFormData({...formData, age: e.target.value})}
                          className="bg-black/20 border-white/10" required
                      />
                    </div>
                    <div className="grid gap-2">
                      <Label>Position</Label>
                      <Input
                          value={formData.position}
                          onChange={(e) => setFormData({...formData, position: e.target.value})}
                          className="bg-black/20 border-white/10" required
                      />
                    </div>
                  </div>

                  <div className="grid grid-cols-2 gap-4">
                    <div className="grid gap-2">
                      <Label>Rating</Label>
                      <Input
                          type="number" step="0.1"
                          value={formData.rating}
                          onChange={(e) => setFormData({...formData, rating: e.target.value})}
                          className="bg-black/20 border-white/10" required
                      />
                    </div>
                    <div className="grid gap-2">
                      <Label>Jersey #</Label>
                      <Input
                          type="number"
                          value={formData.jerseyNumber}
                          onChange={(e) => setFormData({...formData, jerseyNumber: e.target.value})}
                          className="bg-black/20 border-white/10" required
                      />
                    </div>
                  </div>

                  <div className="grid gap-2">
                    <Label>Team</Label>
                    <Select
                        value={formData.teamId}
                        onValueChange={(val) => setFormData({...formData, teamId: val})}
                    >
                      <SelectTrigger className="bg-black/20 border-white/10 text-white">
                        <SelectValue placeholder="Select a team" />
                      </SelectTrigger>
                      <SelectContent className="bg-[#1a1b1e] border-white/10 text-white">
                        <SelectItem value="free">Free Agent</SelectItem>
                        {teams.map(team => (
                            <SelectItem key={team.id} value={team.id.toString()}>
                              {team.name}
                            </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                  </div>

                  <DialogFooter className="mt-4">
                    <Button type="submit" disabled={isSubmitting} className="bg-blue-600 hover:bg-blue-700 w-full text-white">
                      {isSubmitting ? <Loader2 className="w-4 h-4 animate-spin mr-2" /> : null}
                      {editingId ? "Update Player" : "Save Player"}
                    </Button>
                  </DialogFooter>
                </form>
              </DialogContent>
            </Dialog>
          </div>
        </div>

        {/* TABLE */}
        <div className="rounded-xl border border-white/10 bg-black/20 overflow-hidden backdrop-blur-sm shadow-xl">
          <div className="overflow-x-auto">
            <table className="w-full text-sm text-left text-gray-300">
              <thead className="text-xs uppercase bg-white/5 text-muted-foreground border-b border-white/5">
              <tr>
                <th className="px-6 py-4 font-medium cursor-pointer hover:text-white" onClick={() => handleSort('firstName')}>Player</th>
                <th className="px-6 py-4 font-medium">Position</th>
                <th className="px-6 py-4 font-medium">Age</th>
                <th className="px-6 py-4 font-medium cursor-pointer hover:text-white" onClick={() => handleSort('rating')}>Rating</th>
                <th className="px-6 py-4 font-medium">Jersey #</th>
                <th className="px-6 py-4 font-medium">Team</th>
                <th className="px-6 py-4 text-right">Actions</th>
              </tr>
              </thead>
              <tbody className="divide-y divide-white/5">
              <AnimatePresence>
                {loading ? (
                    <tr><td colSpan={7} className="px-6 py-12 text-center"><Loader2 className="w-6 h-6 animate-spin mx-auto text-blue-500"/></td></tr>
                ) : sortedPlayers.length === 0 ? (
                    <tr><td colSpan={7} className="px-6 py-12 text-center text-muted-foreground">No players found.</td></tr>
                ) : (
                    sortedPlayers.map((player) => {
                      if (!player) return null;
                      const teamName = getTeamName(player.teamId);

                      return (
                          <motion.tr
                              key={player.id}
                              initial={{ opacity: 0 }}
                              animate={{ opacity: 1 }}
                              exit={{ opacity: 0 }}
                              className="hover:bg-white/5 transition-colors group"
                          >
                            <td className="px-6 py-4 font-medium text-white flex items-center gap-3">
                              <div className={`w-9 h-9 rounded-lg flex items-center justify-center font-bold text-xs border ${getRatingBg(player.rating)} ${getRatingColor(player.rating)}`}>
                                {(player.firstName?.[0] || '')}{(player.lastName?.[0] || '')}
                              </div>
                              <div>
                                <div className="font-semibold">{player.firstName} {player.lastName}</div>
                                <div className="text-xs text-muted-foreground font-normal">ID: #{player.id}</div>
                              </div>
                            </td>
                            <td className="px-6 py-4"><span className="px-2.5 py-1 rounded-md text-xs bg-white/10">{player.position}</span></td>
                            <td className="px-6 py-4">{player.age}</td>
                            <td className="px-6 py-4">
                              <div className="flex items-center gap-1.5">
                                <Star className={`w-3.5 h-3.5 ${getRatingColor(player.rating)}`} fill="currentColor" />
                                <span className={`font-bold ${getRatingColor(player.rating)}`}>{player.rating}</span>
                              </div>
                            </td>
                            <td className="px-6 py-4 font-mono">#{player.jerseyNumber}</td>
                            <td className="px-6 py-4">
                              {teamName ? (
                                  <div className="flex items-center gap-2"><span className="w-1.5 h-1.5 rounded-full bg-green-500 shadow-green-500/50" /><span className="text-gray-300">{teamName}</span></div>
                              ) : (
                                  <div className="flex items-center gap-2"><span className="w-1.5 h-1.5 rounded-full bg-orange-500" /><span className="text-orange-400">Free Agent</span></div>
                              )}
                            </td>

                            {/* CRUD ACTIONS */}
                            <td className="px-6 py-4 text-right">
                              <DropdownMenu>
                                <DropdownMenuTrigger asChild>
                                  <Button variant="ghost" size="icon" className="h-8 w-8 text-muted-foreground hover:text-white">
                                    <MoreHorizontal className="w-4 h-4" />
                                  </Button>
                                </DropdownMenuTrigger>
                                <DropdownMenuContent align="end" className="bg-[#1a1b1e] border-white/10 text-white">
                                  <DropdownMenuItem onClick={() => openEditModal(player)} className="cursor-pointer hover:bg-white/10">
                                    <Pencil className="w-4 h-4 mr-2" /> Edit
                                  </DropdownMenuItem>
                                  <DropdownMenuItem onClick={() => handleDelete(player.id)} className="cursor-pointer text-red-500 hover:bg-red-500/10 hover:text-red-400">
                                    <Trash2 className="w-4 h-4 mr-2" /> Delete
                                  </DropdownMenuItem>
                                </DropdownMenuContent>
                              </DropdownMenu>
                            </td>
                          </motion.tr>
                      );
                    })
                )}
              </AnimatePresence>
              </tbody>
            </table>
          </div>
        </div>
      </div>
  );
}